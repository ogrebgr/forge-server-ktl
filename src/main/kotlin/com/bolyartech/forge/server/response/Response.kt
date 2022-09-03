package com.bolyartech.forge.server.response

import jakarta.servlet.http.HttpServletResponse

interface Response {
    /**
     * Converts Response to HttpServletResponse
     *
     * @param resp HTTP servlet response
     * @return Length of the response
     * @throws ResponseException if there is a problem during converting
     */
    @Throws(ResponseException::class)
    fun toServletResponse(resp: HttpServletResponse): Long
}