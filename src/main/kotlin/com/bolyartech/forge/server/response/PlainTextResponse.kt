package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

class PlainTextResponse(
    str: String,
    cookiesToSet: List<Cookie> = emptyList(),
    headersToAdd: List<HttpHeader> = emptyList(),
    enableGzipSupport: Boolean = true,
    private val statusCode: Int = HttpServletResponse.SC_OK,
) : AbstractStringResponse(str, cookiesToSet, headersToAdd, enableGzipSupport, statusCode) {

    companion object {
        private const val CONTENT_TYPE = "text/html;charset=UTF-8"
    }

    override fun getContentType(): String {
        return CONTENT_TYPE
    }
}