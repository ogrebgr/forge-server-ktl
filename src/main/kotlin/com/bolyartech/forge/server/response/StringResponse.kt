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

    companion object {
        const val MIN_SIZE_FOR_GZIP = 500
    }
}