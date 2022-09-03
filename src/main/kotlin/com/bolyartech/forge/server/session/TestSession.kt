package com.bolyartech.forge.server.session

import java.io.Serializable
import java.util.*

/**
 * Fake session meant to be used in unit tests
 *
 *
 * Please note that methods [.getCreationTime], [.getLastAccessedTime] and
 * [.getMaxInactiveInterval] always return 0, i.e. they have empty implementation
 */
class TestSession : Session {
    private val id: String
    private val vars: MutableMap<String, Any> = HashMap()

    /**
     * Creates new TestSession
     */
    constructor() {
        id = UUID.randomUUID().toString()
    }

    /**
     * Creates new TestSession with specific ID
     *
     * @param id ID to be used
     */
    constructor(id: String) {
        this.id = id
    }

    override fun getId(): String {
        return ""
    }

    override fun <T> getVar(varName: String): T {
        return vars[varName] as T
    }

    override fun <T : Serializable> setVar(varName: String, value: T) {
        vars[varName] = value
    }

    override fun removeVar(varName: String) {
        vars.remove(varName)
    }

    override fun getMaxInactiveInterval(): Int {
        return 0
    }

    override fun getCreationTime(): Long {
        return 0
    }

    override fun getLastAccessedTime(): Long {
        return 0
    }
}