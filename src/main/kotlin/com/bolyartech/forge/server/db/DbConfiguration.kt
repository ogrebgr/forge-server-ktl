package com.bolyartech.forge.server.db

data class DbConfiguration(
    val dbDsn: String,
    val dbUsername: String,
    val dbPassword: String,
    val cacheMaxStatements: Int,
    val initialPoolSize: Int,
    val minPoolSize: Int,
    val maxPoolSize: Int,
    val idleConnectionTestPeriod: Int,
    val testConnectionOnCheckIn: Boolean,
    val testConnectionOnCheckout: Boolean,
)