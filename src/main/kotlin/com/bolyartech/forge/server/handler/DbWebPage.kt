package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.misc.TemplateEngine
import com.bolyartech.forge.server.misc.TemplateEngineFactory
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.route.RequestContext
import java.sql.Connection
import java.sql.SQLException

abstract class DbWebPage(templateEngineFactory: TemplateEngineFactory, private val dbPool: DbPool) :
    WebPage(templateEngineFactory, true) {

    abstract fun handlePage(
        ctx: RequestContext,
        dbc: Connection,
        tple: TemplateEngine
    ): Response


    final override fun handlePage(ctx: RequestContext, tple: TemplateEngine): Response {
        try {
            dbPool.connection.use { dbc ->
                return handlePage(ctx, dbc, tple)
            }
        } catch (e: SQLException) {
            throw ResponseException(e)
        }
    }
}