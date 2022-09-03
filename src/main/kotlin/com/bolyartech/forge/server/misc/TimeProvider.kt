package com.bolyartech.forge.server.misc

import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

interface TimeProvider {
    fun getVmTime(): Long
    fun getWallClockTime(): LocalDateTime
    fun getWallClockTimeMillis(): Long
}


class TimeProviderImpl @Inject constructor() : TimeProvider {
    override fun getVmTime(): Long {
        return System.nanoTime() / 1000000
    }

    override fun getWallClockTime(): LocalDateTime {
        return LocalDateTime.now(ZoneOffset.UTC)
    }

    override fun getWallClockTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}