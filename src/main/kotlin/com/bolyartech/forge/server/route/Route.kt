package com.bolyartech.forge.server.route

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.handler.RouteHandlerRuntimeResolved
import com.bolyartech.forge.server.handler.StaticResourceNotFoundException
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

    fun getHandler(): RouteHandler
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
            val status = if (e.cause is StaticResourceNotFoundException) {
                HttpServletResponse.SC_NOT_FOUND
            } else {
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            }

            if (httpResp.getHeader("Content-Length") != null) {
                contentLength = httpResp.getHeader("Content-Length")
            }

            logger.trace("{} -> {}: {} {}", httpReq.remoteAddr, status, getHttpMethod(), httpReq.pathInfo)
            loggerWs.trace(
                "{} - - [{}] \"{} {} {}\" {} {} \"{}\" \"{}\"",
                httpReq.remoteAddr,
                ZonedDateTime.now().format(dateTimeFormatterWebServer),
                getHttpMethod(),
                httpReq.pathInfo,
                httpReq.protocol,
                status,
                contentLength,
                ref,
                ua
            )

            throw ResponseException(e)
        }
    }

    override fun getHandler(): RouteHandler {
        return routeHandler
    }
}


open class RouteExact(httpMethod: HttpMethod, private val path: String, routeHandler: RouteHandler) :
    AbstractRoute(httpMethod, routeHandler) {

    override fun isMatching(urlPath: String): Boolean {
        return urlPath == path
    }

    override fun getPath(): String {
        return path
    }
}

open class RouteStartsWith(httpMethod: HttpMethod, path: String, routeHandler: RouteHandler) :
    RouteExact(httpMethod, path, routeHandler) {

    override fun isMatching(urlPath: String): Boolean {
        return urlPath.startsWith(getPath())
    }
}


open class RouteRuntimeResolved(
    httpMethod: HttpMethod,
    private val pathPatternPrefix: String,
    routeHandler: RouteHandlerRuntimeResolved
) :
    AbstractRoute(httpMethod, routeHandler) {

    override fun isMatching(urlPath: String): Boolean {
        return if (urlPath.startsWith(getPath())) {
            (routeHandler as RouteHandlerRuntimeResolved).willingToHandle(urlPath.substring(pathPatternPrefix.length))
        } else {
            false
        }
    }

    override fun getPath(): String {
        return pathPatternPrefix
    }
}

class GetRouteExact(path: String, routeHandler: RouteHandler) : RouteExact(HttpMethod.GET, path, routeHandler)
class PostRouteExact(path: String, routeHandler: RouteHandler) : RouteExact(HttpMethod.POST, path, routeHandler)
class PutRouteExact(path: String, routeHandler: RouteHandler) : RouteExact(HttpMethod.PUT, path, routeHandler)
class DeleteRouteExact(path: String, routeHandler: RouteHandler) : RouteExact(HttpMethod.DELETE, path, routeHandler)

class GetRouteRuntimeResolved(pathPatternPrefix: String, routeHandler: RouteHandlerRuntimeResolved) :
    RouteRuntimeResolved(HttpMethod.GET, pathPatternPrefix, routeHandler)

class PostRouteRuntimeResolved(pathPatternPrefix: String, routeHandler: RouteHandlerRuntimeResolved) :
    RouteRuntimeResolved(HttpMethod.POST, pathPatternPrefix, routeHandler)

class PutRouteRuntimeResolved(pathPatternPrefix: String, routeHandler: RouteHandlerRuntimeResolved) :
    RouteRuntimeResolved(HttpMethod.PUT, pathPatternPrefix, routeHandler)

class DeleteRouteRuntimeResolved(pathPatternPrefix: String, routeHandler: RouteHandlerRuntimeResolved) :
    RouteRuntimeResolved(HttpMethod.DELETE, pathPatternPrefix, routeHandler)

class GetRouteStartsWith(path: String, routeHandler: RouteHandler) : RouteStartsWith(HttpMethod.GET, path, routeHandler)
class PostRouteStartsWith(path: String, routeHandler: RouteHandler) : RouteStartsWith(HttpMethod.POST, path, routeHandler)
class PutRouteStartsWith(path: String, routeHandler: RouteHandler) : RouteStartsWith(HttpMethod.PUT, path, routeHandler)
class DeleteRouteStartsWith(path: String, routeHandler: RouteHandler) : RouteStartsWith(HttpMethod.DELETE, path, routeHandler)
