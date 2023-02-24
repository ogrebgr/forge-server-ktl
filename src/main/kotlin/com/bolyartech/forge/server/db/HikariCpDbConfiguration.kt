package com.bolyartech.forge.server.db

data class HikariCpDbConfiguration(
    val dbDsn: String,
    val dbUsername: String,
    val dbPassword: String,
    val minPoolSize: Int?,
    val maxPoolSize: Int,
    val prepStmtCacheSize: Int,
    val prepStmtCacheSqlLimit: Int,
    val leakDetectionThresholdMillis: Long?,
)