package com.bolyartech.forge.server.misc

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply

class IgnoreForgeLoggingFilter : Filter<ILoggingEvent>() {
    companion object {
        private const val FORGE_LOGGER = "com.bolyartech.forge.server.webserverlog"
    }

    override fun decide(event: ILoggingEvent): FilterReply {

        return if (event.loggerName == null) {
            FilterReply.NEUTRAL
        } else if (event.loggerName == FORGE_LOGGER) {
            FilterReply.DENY
        } else {
            FilterReply.NEUTRAL
        }
    }
}