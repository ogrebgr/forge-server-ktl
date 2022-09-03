package com.bolyartech.forge.server.response

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

abstract class AbstractResponse : Response {
    private val cookiesToSet: MutableList<Cookie> = ArrayList()
    private val headersToAdd: MutableList<HttpHeader> = ArrayList<HttpHeader>()

    constructor() {}

    constructor(cookiesToSet: List<Cookie>) {
        if (cookiesToSet == null) {
            throw NullPointerException("cookiesToSet is null")
        }
        this.cookiesToSet.addAll(cookiesToSet)
    }

    constructor(cookiesToSet: List<Cookie>?, headersToAdd: List<HttpHeader>?) {
        if (cookiesToSet == null) {
            throw NullPointerException("cookiesToSet is null")
        }
        if (headersToAdd == null) {
            throw NullPointerException("headersToAdd is null")
        }
        this.cookiesToSet.addAll(cookiesToSet)
        this.headersToAdd.addAll(headersToAdd)
    }

    protected fun addCookiesAndHeaders(resp: HttpServletResponse) {
        for (c in cookiesToSet) {
            resp.addCookie(c)
        }
        for (h in headersToAdd) {
            resp.addHeader(h.header, h.value)
        }
    }
}