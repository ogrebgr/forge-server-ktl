package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.route.RequestContext
import java.sql.Connection
import java.sql.SQLException

abstract class DbEndpoint(private val dbPool: DbPool) : RouteHandler {
    abstract fun handle(ctx: RequestContext, dbc: Connection): Response

    final override fun handle(ctx: RequestContext): Response {
        try {
            dbPool.connection.use { dbc ->
                return handle(ctx, dbc)
            }
        } catch (e: SQLException) {
            throw ResponseException(e)
        }
    }
}

