package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse


open class HtmlResponseBuilder constructor(code: Int) :
    AbstractResponseBuilder(code) {

    private var body: String = ""

    fun body(body: String): HtmlResponseBuilder {
        this.body = body

        return this
    }


    override fun build(): HtmlResponse {
        return HtmlResponse(body, getCookies(), getHeaders(), isGzipSupportEnabled())
    }
}