package com.bolyartech.forge.server.session

import java.io.Serializable

interface Session {
    /**
     * Returns the unique session ID
     *
     * @return session ID
     */
    fun getId(): String

    /**
     * Returns the value of a session variable (if previously set)
     *
     * @param varName Variable name
     * @param <T>     Type of the value (inferred)
     * @return Value of the session variable or null if not set
    </T> */
    fun <T> getVar(varName: String): T

    /**
     * Sets session variable
     *
     * @param varName Variable name
     * @param value   Value of the variable
     */
    fun <T : Serializable> setVar(varName: String, value: T)

    /**
     * Removes the object bound with the specified variable name from this session. If the session does not have an
     * object bound with the specified name, this method does nothing.
     *
     * @param varName name of the variable to be removed
     */
    fun removeVar(varName: String)

    /**
     * Returns max inactive interval of the session.
     * If no request are made during that interval session expires
     *
     * @return max inactive interval of the session
     */
    fun getMaxInactiveInterval(): Int


    /**
     * Returns the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
     *
     * @return a long specifying when this session was created, expressed in milliseconds since 1/1/1970 GMT
     */
    fun getCreationTime(): Long

    /**
     * Returns the last time the client sent a request associated with this session, as the number of milliseconds since
     * midnight January 1, 1970 GMT, and marked by the time the container received the request.
     *
     *
     * Actions that your application takes, such as getting or setting a value associated with the session, do not
     * affect the access time.
     *
     * @return a long representing the last time the client sent a request associated with this session, expressed in milliseconds since 1/1/1970 GMT
     */
    fun getLastAccessedTime(): Long
}