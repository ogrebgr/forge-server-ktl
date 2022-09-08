package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.misc.TemplateEngine
import com.bolyartech.forge.server.misc.TemplateEngineFactory
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.route.RequestContext

/**
 * Handler which provides normal web page functionality.
 * Use [.createHtmlResponse] to create HTML response.
 */
abstract class WebPage(
    private val templateEngineFactory: TemplateEngineFactory,
    private val enableGzipSupport: Boolean = true
) : RouteHandler {
    /**
     * Handles a HTTP request wrapped as [RequestContext] and produces Response.
     *
     * @param ctx  Request context
     * @param tple Template engine
     * @return Response
     * @throws ResponseException if there is a problem handling the request
     */
    @Throws(ResponseException::class)
    abstract fun handlePage(ctx: RequestContext, tple: TemplateEngine): Response


    final override fun handle(ctx: RequestContext): Response {
        return handlePage(ctx, templateEngineFactory.createNew())
    }
}