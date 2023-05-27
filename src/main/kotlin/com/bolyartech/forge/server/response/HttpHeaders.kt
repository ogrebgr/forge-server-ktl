package com.bolyartech.forge.server.response

/**
 * Common HTTP headers
 */
interface HttpHeaders {
    companion object {
        const val CONNECTION = "Connection"
        const val HOST = "Host"
        const val REFERRER = "Referer"
        const val USER_AGENT = "User-Agent"
        const val LAST_MODIFIED = "Last-Modified"
        const val CONTENT_TYPE = "Content-Type"
        const val CONTENT_TYPE_JSON = "application/json"
        const val CONTENT_TYPE_OCTET = "application/octet-stream"
        const val CACHE_CONTROL = "Cache-control"
        const val CACHE_CONTROL_VALUE_NO_CACHE = "no-cache"
        const val CONTENT_ENCODING = "Content-Encoding"
        const val CONTENT_ENCODING_GZIP = "gzip"
        const val ACCEPT = "Accept"
        const val ACCEPT_CHARSET = "Accept-Charset"
        const val ACCEPT_ENCODING = "Accept-Encoding"
        const val ACCEPT_LANGUAGE = "Accept-Language"
        const val CONTENT_DISPOSITION = "Content-Disposition"
        const val CONTENT_DISPOSITION_ATTACHMENT = "attachment; filename=\"{0}\""
    }
}