package com.bolyartech.forge.server.session

import jakarta.servlet.http.HttpSession
import java.io.Serializable

/**
 * Session
 */
class SessionImpl(private val httpSession: HttpSession) : Session {
    override fun getId(): String {
        return httpSession.id
    }

    override fun <T> getVar(varName: String): T? {
        @Suppress("UNCHECKED_CAST")
        return httpSession.getAttribute(varName) as T
    }

    override fun <T : Serializable> setVar(varName: String, value: T) {
        httpSession.setAttribute(varName, value)
    }

    override fun removeVar(varName: String) {
        httpSession.removeAttribute(varName)
    }

    override fun getMaxInactiveInterval(): Int {
        return httpSession.maxInactiveInterval
    }

    override fun getCreationTime(): Long {
        return httpSession.creationTime
    }

    override fun getLastAccessedTime(): Long {
        return httpSession.lastAccessedTime
    }
}