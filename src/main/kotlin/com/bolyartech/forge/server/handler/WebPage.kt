package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.misc.TemplateEngine
import com.bolyartech.forge.server.misc.TemplateEngineFactory
import com.bolyartech.forge.server.response.HtmlResponse
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.route.RequestContext

/**
 * Handler that produces HTML content, i.e. simple web page
 */
abstract class WebPage : RouteHandler {
    private val templateEngineFactory: TemplateEngineFactory
    private val enableGzipSupport: Boolean

    /**
     * Creates new WebPage
     *
     * @param templateEngineFactory Template engine factory
     */
    constructor(templateEngineFactory: TemplateEngineFactory) {
        this.templateEngineFactory = templateEngineFactory
        enableGzipSupport = false
    }

    /**
     * Creates new WebPage
     *
     * @param templateEngineFactory Template engine factory
     * @param enableGzipSupport     if true Gzip compression will be used (if supported by the client)
     */
    constructor(templateEngineFactory: TemplateEngineFactory, enableGzipSupport: Boolean) {
        this.templateEngineFactory = templateEngineFactory
        this.enableGzipSupport = enableGzipSupport
    }

    /**
     * Handles a HTTP request wrapped as [RequestContext] and produces HTML
     *
     * @param ctx  Request context
     * @param tple Template engine
     * @return HTML
     * @throws ResponseException if there is a problem handling the request
     */
    @Throws(ResponseException::class)
    abstract fun produceHtml(ctx: RequestContext, tple: TemplateEngine): String
    override fun handle(ctx: RequestContext): Response {
        val content = produceHtml(ctx, templateEngineFactory.createNew())
        return HtmlResponse(content, enableGzipSupport)
    }

    /**
     * Returns the template engine factory
     *
     * @return template engine factory
     */
    fun getTemplateEngineFactory(): TemplateEngineFactory {
        return templateEngineFactory
    }
}