package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.route.RequestContext

interface RouteHandler {
    @Throws(ResponseException::class)
    fun handle(ctx: RequestContext): Response

}

interface RouteHandlerFlexible : RouteHandler {
    fun willingToHandle(pathInfo: String): Boolean
}