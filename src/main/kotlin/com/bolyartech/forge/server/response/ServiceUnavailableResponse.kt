package com.bolyartech.forge.server.response

import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.PrintWriter

class ServiceUnavailableResponse : Response {
    companion object {
        private const val body = "503 Service unavailable"
    }

    @Throws(ResponseException::class)
    override fun toServletResponse(resp: HttpServletResponse): Long {
        resp.status = HttpServletResponse.SC_SERVICE_UNAVAILABLE
        val pw: PrintWriter
        try {
            pw = resp.writer
            pw.print(body)
            pw.flush()
            pw.close()

            return body.length.toLong()
        } catch (e: IOException) {
            throw ResponseException(e)
        } catch (e: Exception) {
            throw ResponseException(e)
        }
    }
}