package com.bolyartech.forge.server.misc

import com.bolyartech.forge.server.db.DbPool
import javax.inject.Inject
import kotlin.concurrent.thread

interface AsyncTaskExecutor {
    fun execute(task: AsyncTask, ttlMillis: Int = 5000): AsyncTaskData
    fun ack(id: Int, token: String)
    fun acknowledge(id: Int, token: String)
}

@Deprecated("Use AsyncTaskManager")
class AsyncTaskExecutorImpl @Inject constructor(private val dbPool: DbPool, private val asyncTaskDataDbh: AsyncTaskDataDbh) :
    AsyncTaskExecutor {
    override fun execute(task: AsyncTask, ttlMillis: Int): AsyncTaskData {
        dbPool.connection.use { dbc ->
            val rec = asyncTaskDataDbh.createNew(dbc, ttlMillis)
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
