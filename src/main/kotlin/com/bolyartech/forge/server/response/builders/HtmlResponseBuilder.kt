package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse


open class HtmlResponseBuilder constructor() :
    AbstractResponseBuilder() {

    private var body: String = ""
    private var enableGzipSupport: Boolean = true

    fun body(body: String): HtmlResponseBuilder {
        this.body = body

        return this
    }

    fun gzipSupport(enable: Boolean) {
        enableGzipSupport = enable
    }

    override fun build(): HtmlResponse {
        if (getStatus() < 0) {
            throw java.lang.IllegalStateException("Status not set. Call status()")
        }
        return HtmlResponse(getCookies(), getHeaders(), body, enableGzipSupport)
    }
}