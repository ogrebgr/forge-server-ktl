package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse
import jakarta.servlet.http.HttpServletResponse

class NotFoundHtmlResponseBuilder(private val body: String) : HtmlResponseBuilder(HttpServletResponse.SC_NOT_FOUND) {
    override fun build(): HtmlResponse {
        body(body)

        return super.build()
    }
}