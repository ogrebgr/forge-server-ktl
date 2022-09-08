package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse
import jakarta.servlet.http.HttpServletResponse

class NotFoundHtmlResponseBuilder(private val body: String) : HtmlResponseBuilder() {
    override fun build(): HtmlResponse {
        status(HttpServletResponse.SC_NOT_FOUND)
        body(body)

        return build()
    }
}