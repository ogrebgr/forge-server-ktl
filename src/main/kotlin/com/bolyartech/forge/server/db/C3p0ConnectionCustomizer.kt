package com.bolyartech.forge.server.db

import com.mchange.v2.c3p0.AbstractConnectionCustomizer
import java.sql.Connection

/**
 * C3p0 connection customizer which set the transaction isolation level to
 * `Connection.TRANSACTION_READ_COMMITTED`
 */
class C3p0ConnectionCustomizer : AbstractConnectionCustomizer() {
    @Throws(Exception::class)
    override fun onAcquire(c: Connection, parentDataSourceIdentityToken: String) {
        super.onAcquire(c, parentDataSourceIdentityToken)
        c.transactionIsolation = Connection.TRANSACTION_READ_COMMITTED
    }
}