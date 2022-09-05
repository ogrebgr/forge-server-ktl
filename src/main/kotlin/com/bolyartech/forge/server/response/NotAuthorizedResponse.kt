package com.bolyartech.forge.server.response

import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.PrintWriter

class NotAuthorizedResponse : Response {
    companion object {
        private const val body = "401 Not authorized"
    }

    @Throws(ResponseException::class)
    override fun toServletResponse(resp: HttpServletResponse): Long {
        resp.status = HttpServletResponse.SC_UNAUTHORIZED
        val pw: PrintWriter
        try {
            pw = resp.writer
            pw.print(body)
            pw.flush()
            pw.close()

            return body.length.toLong()
        } catch (e: IOException) {
            throw ResponseException(e)
        }
    }
}