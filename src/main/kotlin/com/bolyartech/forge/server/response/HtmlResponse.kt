package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie

/**
 * HTML str response
 */
open class HtmlResponse : AbstractStringResponse {
    companion object {
        private const val CONTENT_TYPE = "text/html;charset=UTF-8"
    }

    /**
     * Creates new HtmlResponse
     *
     * @param str HTML of the response
     */
    constructor(str: String) : super(str)

    constructor(cookiesToSet: List<Cookie>, str: String) : super(cookiesToSet, str)

    constructor(cookiesToSet: List<Cookie>, headersToAdd: List<HttpHeader>, str: String, enableGzipSupport: Boolean) : super(
        cookiesToSet,
        headersToAdd,
        str,
        enableGzipSupport
    ) {
    }

    constructor(cookiesToSet: List<Cookie>, str: String, enableGzipSupport: Boolean) : super(
        cookiesToSet,
        str,
        enableGzipSupport
    ) {
    }

    /**
     * Creates new HtmlResponse
     *
     * @param str            HTML of the response
     * @param enableGzipSupport if true Gzip compression will be used if the client supports it
     */
    constructor(str: String, enableGzipSupport: Boolean) : super(str, enableGzipSupport) {}

    override fun getContentType(): String {
        return CONTENT_TYPE
    }
}