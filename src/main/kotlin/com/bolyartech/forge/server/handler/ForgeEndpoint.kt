package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.route.RequestContext

abstract class ForgeEndpoint() : RouteHandler {
    @Throws(ResponseException::class)
    abstract fun handleForge(ctx: RequestContext): ForgeResponse


    final override fun handle(ctx: RequestContext): ForgeResponse {
        return handleForge(ctx)
    }
}