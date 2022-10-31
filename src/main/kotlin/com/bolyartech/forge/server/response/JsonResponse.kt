package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie

/**
 * JSON string response
 */
open class JsonResponse(
    str: String,
    cookiesToSet: List<Cookie> = emptyList(),
    headersToAdd: List<HttpHeader> = emptyList(),
    enableGzipSupport: Boolean = true
) : AbstractStringResponse(str, cookiesToSet, headersToAdd, enableGzipSupport) {

    override fun getContentType(): String {
        return CONTENT_TYPE_JSON
    }

    companion object {
        private const val CONTENT_TYPE_JSON = "application/json;charset=UTF-8"
    }
}