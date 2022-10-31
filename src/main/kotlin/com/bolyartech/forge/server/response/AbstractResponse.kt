package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

abstract class AbstractResponse(
    private val cookiesToSet: List<Cookie>,
    private val headersToAdd: List<HttpHeader>
) : Response {
    protected fun addCookiesAndHeaders(resp: HttpServletResponse) {
        for (c in cookiesToSet) {
            resp.addCookie(c)
        }
        for (h in headersToAdd) {
            resp.addHeader(h.header, h.value)
        }
    }
}