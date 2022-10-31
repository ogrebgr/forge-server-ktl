package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie

/**
 * HTML str response
 */
open class HtmlResponse(
    str: String,
    cookiesToSet: List<Cookie> = emptyList(),
    headersToAdd: List<HttpHeader> = emptyList(),
    enableGzipSupport: Boolean = true
) : AbstractStringResponse(str, cookiesToSet, headersToAdd, enableGzipSupport) {
    companion object {
        private const val CONTENT_TYPE = "text/html;charset=UTF-8"
    }


    override fun getContentType(): String {
        return CONTENT_TYPE
    }
}