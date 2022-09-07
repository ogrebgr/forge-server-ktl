package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie

/**
 * JSON string response
 */
open class JsonResponse : AbstractStringResponse {
    constructor(str: String) : super(str) {}
    constructor(cookiesToSet: List<Cookie>, str: String) : super(cookiesToSet, str) {}
    constructor(cookiesToSet: List<Cookie>, str: String, enableGzipSupport: Boolean) : super(
        cookiesToSet,
        str,
        enableGzipSupport
    ) {
    }

    /**
     * Creates new JsonResponse
     *
     * @param str            String response, i.e. the JSON
     * @param enableGzipSupport if true Gzip compression will be used if the client supports it
     */
    constructor(str: String, enableGzipSupport: Boolean) : super(str, enableGzipSupport) {}

    override fun getContentType(): String {
        return CONTENT_TYPE_JSON
    }

    companion object {
        private const val CONTENT_TYPE_JSON = "application/json;charset=UTF-8"
    }
}