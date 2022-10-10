package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse
import jakarta.servlet.http.HttpServletResponse

class InternalServerErrorHtmlResponseBuilder(private val body: String) :
    HtmlResponseBuilder(HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {

    override fun build(): HtmlResponse {
        body(body)

        return super.build()
    }
}