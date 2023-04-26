package com.bolyartech.forge.server.misc

import com.bolyartech.forge.server.misc.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Qualifier

interface AsyncTaskManager : AsyncTaskExecutor, Subserver {
    fun getTaskData(id: Int, token: String): AsyncTaskData?
}

class AsyncTaskManagerImpl @Inject constructor(
    @AsyncTaskManagerExecutor private val executor: ExecutorService,
    @AsyncTaskManagerScheduler private val scheduler: ScheduledExecutorService,
    @AsyncTaskManagerMaintenanceIntervalMillis private val maintenanceIntervalMillis: Long,
    private val timeProvider: TimeProvider,
) : AsyncTaskManager {

    private val idSequence = AtomicInteger(0)
    private val map = ConcurrentHashMap<Int, AsyncTaskData>()

    override fun getTaskData(id: Int, token: String): AsyncTaskData? {
        val data = map[id] ?: return null
        return if (data.token == token) {
            data
        } else {
            null
        }
    }

    override fun ack(id: Int, token: String) {
        acknowledge(id, token)
    }

    override fun acknowledge(id: Int, token: String) {
        val data = map[id] ?: return
        if (data.token == token) {
            map.remove(id)
        }
    }

    override fun execute(task: AsyncTask, ttl: Int): AsyncTaskData {
        val ret = AsyncTaskData(
            idSequence.incrementAndGet(),
            AsyncTaskData.State.NEW,
            timeProvider.getWallClockTime(),
            ttl,
            null,
            UUID.randomUUID().toString()
        )

        map[ret.id] = ret

        executor.submit {
            try {
                val data = map[ret.id] ?: throw IllegalStateException("Cannot find data")

                when (val out = task.run()) {
                    is AsyncTaskOutcomeError -> {
                        endedInError(data, out.data)
                    }

                    is AsyncTaskOutcomeOk -> {
                        map[ret.id] = AsyncTaskData(
                            data.id,
                            AsyncTaskData.State.ENDED_OK,
                            data.ts,
                            data.ttl,
                            out.data,
                            data.token
                        )
                    }
                }
            } catch (e: Exception) {
                val data = map[ret.id] ?: return@submit
                endedInError(data, "Exception: " + e.message)
            }
        }

        return ret
    }

    private fun endedInError(taskData: AsyncTaskData, errorString: String?) {
        map[taskData.id] = AsyncTaskData(
            taskData.id,
            AsyncTaskData.State.ENDED_ERROR,
            taskData.ts,
            taskData.ttl,
            errorString,
            taskData.token
        )
    }

    override fun start() {
        scheduler.scheduleAtFixedRate(
            createMaintenanceRunnable(),
            maintenanceIntervalMillis,
            maintenanceIntervalMillis,
            TimeUnit.MILLISECONDS
        )
    }

    private fun createMaintenanceRunnable(): Runnable {
        return Runnable {
            val it = map.iterator()
            val now = timeProvider.getWallClockTime()
            while(it.hasNext()) {
                val item = it.next()
                if (item.value.ts.plusNanos(TimeUnit.MILLISECONDS.toNanos(item.value.ttl.toLong())).isBefore(now)) {
                    map.remove(item.value.id)
                }
            }
        }
    }

    override fun shutdown() {
        scheduler.shutdown()
        executor.shutdown()
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AsyncTaskManagerExecutor

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AsyncTaskManagerScheduler

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AsyncTaskManagerMaintenanceIntervalMillis