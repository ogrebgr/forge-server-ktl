package com.bolyartech.forge.server.db

import com.mchange.v2.c3p0.ComboPooledDataSource
import java.sql.Connection

/**
 * C3p0 implementation od [DbPool]
 */
class C3p0DbPool(private val dataSource: ComboPooledDataSource) : DbPool {
    override val connection: Connection
        get() = dataSource.connection
}