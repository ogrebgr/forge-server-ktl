package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

/**
 * JSON string response
 */
open class JsonResponse(
    str: String,
    cookiesToSet: List<Cookie> = emptyList(),
    headersToAdd: List<HttpHeader> = emptyList(),
    enableGzipSupport: Boolean = true,
    private val statusCode: Int = HttpServletResponse.SC_OK,
) : AbstractStringResponse(str, cookiesToSet, headersToAdd, enableGzipSupport, statusCode) {

    override fun getContentType(): String {
        return CONTENT_TYPE_JSON
    }

    companion object {
        private const val CONTENT_TYPE_JSON = "application/json;charset=UTF-8"
    }
}