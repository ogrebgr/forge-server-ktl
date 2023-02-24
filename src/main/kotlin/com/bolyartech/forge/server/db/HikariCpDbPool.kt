package com.bolyartech.forge.server.db

import java.sql.Connection
import javax.sql.DataSource

class HikariCpDbPool(private val dataSource: DataSource) : DbPool {
    override val connection: Connection
        get() = dataSource.connection
}