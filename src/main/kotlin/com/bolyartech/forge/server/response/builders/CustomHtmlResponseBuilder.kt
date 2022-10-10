package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse

open class CustomHtmlResponseBuilder(code: Int, private val body: String = "") : HtmlResponseBuilder(code) {
    override fun build(): HtmlResponse {
        body(body)

        return super.build()
    }
}