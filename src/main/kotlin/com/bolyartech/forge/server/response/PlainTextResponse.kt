package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie

class PlainTextResponse : AbstractStringResponse {
    constructor(str: String) : super(str)
    constructor(cookiesToSet: List<Cookie>, str: String) : super(cookiesToSet, str)
    constructor(cookiesToSet: List<Cookie>, headersToAdd: List<HttpHeader>, str: String, enableGzipSupport: Boolean) : super(
        cookiesToSet,
        headersToAdd,
        str,
        enableGzipSupport
    )

    constructor(cookiesToSet: List<Cookie>, str: String, enableGzipSupport: Boolean) : super(cookiesToSet, str, enableGzipSupport)
    constructor(str: String, enableGzipSupport: Boolean) : super(str, enableGzipSupport)

    companion object {
        private const val CONTENT_TYPE = "text/html;charset=UTF-8"
    }

    override fun getContentType(): String {
        return CONTENT_TYPE
    }
}