package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse
import jakarta.servlet.http.HttpServletResponse

class InternalServerErrorHtmlResponseBuilder(private val body: String) : HtmlResponseBuilder() {
    override fun build(): HtmlResponse {
        status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        body(body)

        return build()
    }
}