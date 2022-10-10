package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HtmlResponse
import jakarta.servlet.http.HttpServletResponse

class OkHtmlResponseBuilder(private val body: String) : HtmlResponseBuilder(HttpServletResponse.SC_OK) {
    override fun build(): HtmlResponse {
        body(body)

        return super.build()
    }
}