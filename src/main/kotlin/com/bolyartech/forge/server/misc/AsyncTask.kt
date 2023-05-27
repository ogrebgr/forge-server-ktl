package com.bolyartech.forge.server.misc

import java.time.LocalDateTime
import java.util.*

interface AsyncTask {
    fun run(): AsyncTaskOutcome
}

class AsyncTaskException(msg: String) : Exception(msg)

sealed class AsyncTaskOutcome
data class AsyncTaskOutcomeOk(val data: String? = null) : AsyncTaskOutcome()
data class AsyncTaskOutcomeError(val data: String? = null) : AsyncTaskOutcome()


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

