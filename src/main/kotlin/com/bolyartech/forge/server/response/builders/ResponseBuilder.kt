package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.HttpHeader
import com.bolyartech.forge.server.response.Response
import jakarta.servlet.http.Cookie

interface ResponseBuilder {
    fun build(): Response

    fun getStatusCode(): Int

    fun addCookie(c: Cookie): ResponseBuilder
    fun cookieExists(cookieName: String): Boolean
    fun getCookies(): List<Cookie>
    fun removeCookie(cookieName: String): ResponseBuilder

    fun headerExists(headerName: String): Boolean
    fun addHeader(header: HttpHeader): ResponseBuilder
    fun getHeaders(): List<HttpHeader>
    fun removeHeader(headerName: String): ResponseBuilder

    fun gzipSupport(enable: Boolean): ResponseBuilder
    fun isGzipSupportEnabled(): Boolean
}


abstract class AbstractResponseBuilder constructor(private val statusCode: Int) : ResponseBuilder {
    private var enableGzipSupport = true
    private val cookies: MutableMap<String, Cookie> = mutableMapOf<String, Cookie>()
    private val headers: MutableMap<String, HttpHeader> = mutableMapOf<String, HttpHeader>()

    override fun getStatusCode(): Int {
        return statusCode
    }

    override fun getCookies(): List<Cookie> {
        return cookies.values.toList()
    }

    @Throws(CookieAlreadyExistException::class)
    override fun addCookie(c: Cookie): ResponseBuilder {
        if (cookies[c.name.lowercase()] != null) {
            throw CookieAlreadyExistException(c.name)
        }

        cookies[c.name.lowercase()] = c

        return this
    }

    override fun cookieExists(cookieName: String): Boolean {
        return cookies[cookieName.lowercase()] != null
    }

    override fun removeCookie(cookieName: String): ResponseBuilder {
        cookies.remove(cookieName.lowercase())
        return this
    }

    override fun getHeaders(): List<HttpHeader> {
        return headers.values.toList()
    }

    override fun removeHeader(headerName: String): ResponseBuilder {
        headers.remove(headerName.lowercase())
        return this
    }

    @Throws(HeaderAlreadyExistException::class)
    override fun addHeader(header: HttpHeader): ResponseBuilder {
        if (headers[header.header] != null) {
            throw HeaderAlreadyExistException(header.header)
        }

        return this
    }

    override fun headerExists(headerName: String): Boolean {
        return cookies[headerName] != null
    }

    override fun gzipSupport(enable: Boolean): ResponseBuilder {
        enableGzipSupport = enable
        return this
    }

    override fun isGzipSupportEnabled(): Boolean {
        return enableGzipSupport
    }

    class CookieAlreadyExistException(cookieName: String) : Exception("Cookie already exist: $cookieName")
    class HeaderAlreadyExistException(headerName: String) : Exception("Header already exist: $headerName")
}