package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.db.DbPool
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.response.forge.ForgeResponse
import com.bolyartech.forge.server.route.RequestContext
import java.sql.Connection
import java.sql.SQLException

abstract class ForgeDbEndpoint(private val dbPool: DbPool) : ForgeEndpoint() {
    abstract fun handleForge(ctx: RequestContext, dbc: Connection): ForgeResponse

    final override fun handleForge(ctx: RequestContext): ForgeResponse {
        try {
            dbPool.connection.use { dbc ->
                val ret: ForgeResponse = handleForge(ctx, dbc)
                dbc.close()
                return ret
            }
        } catch (e: SQLException) {
            throw ResponseException(e)
        }
    }
}