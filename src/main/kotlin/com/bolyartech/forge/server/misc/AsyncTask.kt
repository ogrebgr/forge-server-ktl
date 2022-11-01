package com.bolyartech.forge.server.misc

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.db.setValue
import com.bolyartech.forge.server.misc.TimeProvider
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.thread

interface AsyncTask {
    fun run(): AsyncTaskOutcome
}

interface AsyncTaskExecutor {
    fun execute(task: AsyncTask, ttl: Int = 300): AsyncTaskData
    fun ack(id: Int, token: String)
    fun acknowledge(id: Int, token: String)
}

class AsyncTaskException(msg: String) : Exception(msg)

class AsyncTaskExecutorImpl @Inject constructor(private val dbPool: DbPool, private val asyncTaskDataDbh: AsyncTaskDataDbh) :
    AsyncTaskExecutor {
    override fun execute(task: AsyncTask, ttl: Int): AsyncTaskData {
        dbPool.connection.use { dbc ->
            val rec = asyncTaskDataDbh.createNew(dbc, ttl)
            thread {
                dbPool.connection.use { dbc ->
                    try {
                        val out = task.run()
                        if (out is AsyncTaskOutcomeOk) {
                            asyncTaskDataDbh.endedOk(dbc, rec.id, out.data)
                        } else {
                            asyncTaskDataDbh.endedError(dbc, rec.id, (out as AsyncTaskOutcomeError).data)
                        }
                    } catch (e: Exception) {
                        asyncTaskDataDbh.endedError(dbc, rec.id, "Exception: " + e.message)
                    }
                }
            }

            return rec
        }
    }

    override fun ack(id: Int, token: String) {
        acknowledge(id, token)
    }

    override fun acknowledge(id: Int, token: String) {
        dbPool.connection.use { dbc ->
            asyncTaskDataDbh.ack(dbc, id, token)
        }
    }
}

sealed class AsyncTaskOutcome
data class AsyncTaskOutcomeOk(val data: String?) : AsyncTaskOutcome()
data class AsyncTaskOutcomeError(val data: String?) : AsyncTaskOutcome()


data class AsyncTaskData(
    val id: Int,
    val state: State,
    val ts: LocalDateTime,
    val ttl: Int,
    val data: String?,
    val token: String,
) {
    enum class State(val id: Int) {
        NEW(0),
        ENDED_OK(200),
        ENDED_ERROR(400);

        companion object {
            private val MAP: Map<Int, State> = State.values().associateBy { it.id }

            @Throws(java.lang.IllegalArgumentException::class)
            fun fromInt(i: Int): State {
                if (MAP[i] != null) {
                    return MAP[i]!!
                } else {
                    throw IllegalArgumentException("Invalid AsyncTaskData.State code: $i")
                }
            }
        }
    }
}

interface AsyncTaskDataDbh {
    @Throws(SQLException::class)
    fun createNew(dbc: Connection, ttl: Int = 300): AsyncTaskData

    @Throws(SQLException::class)
    fun loadById(dbc: Connection, id: Int): AsyncTaskData?

    @Throws(SQLException::class)
    fun loadAll(dbc: Connection): List<AsyncTaskData>

    @Throws(SQLException::class)
    fun count(dbc: Connection): Int

    @Throws(SQLException::class)
    fun delete(dbc: Connection, id: Int): Int

    @Throws(SQLException::class)
    fun deleteAll(dbc: Connection): Int

    @Throws(SQLException::class)
    fun endedOk(dbc: Connection, id: Int, data: String?): Boolean

    @Throws(SQLException::class)
    fun endedError(dbc: Connection, id: Int, data: String?): Boolean

    @Throws(SQLException::class)
    fun ack(dbc: Connection, id: Int, token: String): Boolean
}

class AsyncTaskDataDbhImpl @Inject constructor(
    private val rng: Random,
    private val timeProvider: TimeProvider
) : AsyncTaskDataDbh {

    companion object {
        private const val SQL_INSERT = """INSERT INTO "async_task" ("state", "ts", "ttl", "data", token) VALUES (?, ?, ?, ?, ?)"""
        private const val SQL_SELECT_BY_ID = """SELECT "state", "ts", "ttl", "data", token FROM "async_task" WHERE id = ?"""
        private const val SQL_SELECT_ALL = """SELECT "id", "state", "ts", "ttl", "data" FROM "async_task""""
        private const val SQL_UPDATE =
            """UPDATE "async_task" SET "state" = ?, "ts" = ?, "ttl" = ?, "data" = ? WHERE id = ?"""
        private const val SQL_COUNT = """SELECT COUNT(id) FROM "async_task""""
        private const val SQL_DELETE = """DELETE FROM "async_task" WHERE id = ?"""
        private const val SQL_DELETE_ALL = """DELETE FROM "async_task""""

        private const val SQL_UPDATE_ENDED =
            """UPDATE "async_task" SET "state" = ?, "data" = ? WHERE id = ?"""

        private const val SQL_DELETE_ACK = """DELETE FROM "async_task" WHERE id = ? AND token = ?"""
    }

    @Throws(SQLException::class)
    override fun createNew(dbc: Connection, ttl: Int): AsyncTaskData {
        val token = UUID.randomUUID().toString()
        val ts = timeProvider.getWallClockTime()
        dbc.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS).use {
            it.setValue(1, AsyncTaskData.State.NEW.id)
            it.setValue(2, ts)
            it.setValue(3, ttl)
            it.setInt(4, 0)
            it.setValue(5, token)
            it.executeUpdate()
            it.generatedKeys.use {
                it.next()
                return AsyncTaskData(
                    it.getInt(1),
                    AsyncTaskData.State.NEW,
                    ts,
                    ttl,
                    null,
                    token
                )
            }
        }
    }

    @Throws(SQLException::class)
    override fun loadById(dbc: Connection, id: Int): AsyncTaskData? {
        dbc.prepareStatement(SQL_SELECT_BY_ID).use {
            it.setValue(1, id)

            it.executeQuery().use {
                return if (it.next()) {
                    AsyncTaskData(
                        id,
                        AsyncTaskData.State.fromInt(it.getInt(1)),
                        it.getObject(2, LocalDateTime::class.java),
                        it.getInt(3),
                        it.getString(4),
                        it.getString(5),
                    )
                } else {
                    null
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun loadAll(dbc: Connection): List<AsyncTaskData> {
        val ret = mutableListOf<AsyncTaskData>()
        dbc.prepareStatement(SQL_SELECT_ALL).use {
            it.executeQuery().use {
                while (it.next()) {
                    ret.add(
                        AsyncTaskData(
                            it.getInt(1),
                            AsyncTaskData.State.fromInt(it.getInt(2)),
                            it.getObject(3, LocalDateTime::class.java),
                            it.getInt(4),
                            it.getString(5),
                            it.getString(6),
                        )
                    )
                }
            }
        }

        return ret
    }


    @Throws(SQLException::class)
    override fun count(dbc: Connection): Int {
        dbc.prepareStatement(SQL_COUNT).use {
            it.executeQuery().use {
                it.next()
                return it.getInt(1)
            }
        }
    }

    @Throws(SQLException::class)
    override fun delete(dbc: Connection, id: Int): Int {
        dbc.prepareStatement(SQL_DELETE).use {
            it.setValue(1, id)
            return it.executeUpdate()
        }
    }


    @Throws(SQLException::class)
    override fun deleteAll(dbc: Connection): Int {
        dbc.prepareStatement(SQL_DELETE_ALL).use {
            return it.executeUpdate()
        }
    }

    override fun endedOk(dbc: Connection, id: Int, data: String?): Boolean {
        dbc.prepareStatement(SQL_UPDATE_ENDED).use {
            it.setValue(1, AsyncTaskData.State.ENDED_OK.id)
            it.setValue(2, data)
            it.setValue(3, id)

            return it.executeUpdate() > 0
        }
    }

    override fun endedError(dbc: Connection, id: Int, data: String?): Boolean {
        dbc.prepareStatement(SQL_UPDATE_ENDED).use {
            it.setValue(1, AsyncTaskData.State.ENDED_ERROR.id)
            it.setValue(2, data)
            it.setValue(3, id)

            return it.executeUpdate() > 0
        }
    }

    override fun ack(dbc: Connection, id: Int, token: String): Boolean {
        dbc.prepareStatement(SQL_DELETE_ACK).use {
            it.setValue(1, id)
            it.setValue(1, token)
            return it.executeUpdate() > 0
        }
    }
}