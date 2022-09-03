package com.bolyartech.forge.server.route

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.handler.RouteHandlerPathInfo
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

sealed interface Route {
    /**
     * Returns HTTP method
     *
     * @return HTTP method
     */
    fun getHttpMethod(): HttpMethod

    /**
     * Returns route's path
     *
     * @return route's path
     */
    fun getPath(): String

    /**
     * Handles HTTP request
     *
     * @param httpReq      HTTP request
     * @param httpResp HTTP servlet response
     * @throws ResponseException if there is a problem handling the request
     */
    @Throws(ResponseException::class)
    fun handle(httpReq: HttpServletRequest, httpResp: HttpServletResponse)

    fun isMatching(urlPath: String): Boolean
}

abstract class AbstractRoute(private val httpMethod: HttpMethod, val routeHandler: RouteHandler) : Route {
    override fun getHttpMethod(): HttpMethod {
        return httpMethod
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val loggerWs = LoggerFactory.getLogger("com.bolyartech.forge.server.webserverlog")

    companion object {
        private val dateTimeFormatterWebServer = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z")
    }

    override fun handle(httpReq: HttpServletRequest, httpResp: HttpServletResponse) {
        var ref = "-"
        if (httpReq.getHeader("referer") != null) {
            val refRaw = httpReq.getHeader("referer")
            ref = if (refRaw.length > 255) {
                refRaw.substring(0, 255)
            } else {
                refRaw
            }
        }

        var ua = "-"
        if (httpReq.getHeader("User-Agent") != null) {
            val uaRaw = httpReq.getHeader("User-Agent")
            ua = if (uaRaw.length > 255) {
                uaRaw.substring(0, 255)
            } else {
                uaRaw
            }
        }

        var contentLength = "-"

        try {
            val resp: Response = routeHandler.handle(RequestContextImpl(httpReq, getPath()))
            val cl = resp.toServletResponse(httpResp)
            if (cl > 0) {
                contentLength = cl.toString()
            }
            logger.trace("{} -> {}: {} {}", httpReq.remoteAddr, httpResp.status, getHttpMethod(), httpReq.pathInfo)
            loggerWs.trace(
                "{} - - [{}] \"{} {} {}\" {} {} \"{}\" \"{}\"",
                httpReq.remoteAddr,
                ZonedDateTime.now().format(dateTimeFormatterWebServer),
                getHttpMethod(),
                httpReq.pathInfo,
                httpReq.protocol,
                httpResp.getStatus(),
                contentLength,
                ref,
                ua
            )
        } catch (e: Exception) {
            if (httpResp.getHeader("Content-Length") != null) {
                contentLength = httpResp.getHeader("Content-Length")
            }

            logger.trace("{} -> {}: {} {}", httpReq.remoteAddr, httpResp.getStatus(), getHttpMethod(), httpReq.pathInfo)
            loggerWs.trace(
                "{} - - [{}] \"{} {} {}\" {} {} \"{}\" \"{}\"",
                httpReq.remoteAddr,
                ZonedDateTime.now().format(dateTimeFormatterWebServer),
                getHttpMethod(),
                httpReq.pathInfo,
                httpReq.protocol,
                httpResp.getStatus(),
                contentLength,
                ref,
                ua
            )

            throw ResponseException(e)
        }
    }
}

class RouteSimple(httpMethod: HttpMethod, private val path: String, routeHandler: RouteHandler) :
    AbstractRoute(httpMethod, routeHandler) {

    override fun isMatching(urlPath: String): Boolean {
        val urlPathNorm = RouteRegisterImpl.normalizePath(urlPath)
        return urlPathNorm == path
    }

    override fun getPath(): String {
        return path
    }
}


class RouteFlexible(httpMethod: HttpMethod, private val pathPatternPrefix: String, routeHandler: RouteHandlerPathInfo) :
    AbstractRoute(httpMethod, routeHandler) {

    override fun isMatching(urlPath: String): Boolean {
        val urlPathNorm = RouteRegisterImpl.normalizePath(urlPath)
        if (!urlPathNorm.startsWith(pathPatternPrefix, true)) {
            return false
        }

        return (routeHandler as RouteHandlerPathInfo).willingToHandle(urlPathNorm.substring(pathPatternPrefix.length))
    }

    override fun getPath(): String {
        return pathPatternPrefix
    }
}