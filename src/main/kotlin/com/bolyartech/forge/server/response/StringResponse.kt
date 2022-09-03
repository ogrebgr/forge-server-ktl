package com.bolyartech.forge.server.response

/**
 * String response
 */
interface StringResponse : Response {
    /**
     * Returns the string of the response
     *
     * @return string
     */
    fun getString(): String
}