package com.bolyartech.forge.server.db

import com.bolyartech.forge.server.config.ForgeConfigurationException
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.pathString

class HikariCpDbConfigurationLoaderFile(private val configDirPath: Path) : HikariCpDbConfigurationLoader {
    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val FILENAME = "db.conf"
        private const val PROP_DB_DSN = "db_dsn"
        private const val PROP_DB_USERNAME = "db_username"
        private const val PROP_DB_PASSWORD = "db_password"
        private const val PROP_MIN_POOL_SIZE = "min_pool_size"
        private const val PROP_MAX_POOL_SIZE = "max_pool_size"
        private const val PROP_PREP_STMT_CACHE_SIZE = "prep_stmt_cache_size"
        private const val PROP_PREP_STMT_CACHE_SQL_LIMIT = "prep_stmt_cache_sql_limit"
        private const val PROP_LEAK_DETECTION_THRESHOLD_MILLIS = "leak_detection_threshold_millis"
    }

    override fun load(): HikariCpDbConfiguration {
        val path = Path.of(configDirPath.pathString, FILENAME)
        if (!path.exists()) {
            throw ForgeConfigurationException("Cannot find (${path.pathString})")
        }
        val prop = Properties()
        Files.newInputStream(path).use {
            prop.load(it)
        }

        val minPoolSize = if (prop.getProperty(PROP_MIN_POOL_SIZE).isNullOrBlank()) {
            null
        } else {
            prop.getProperty(PROP_MIN_POOL_SIZE).toInt()
        }

        val detectionThresholdMillis = if (prop.getProperty(PROP_LEAK_DETECTION_THRESHOLD_MILLIS).isNullOrBlank()) {
            null
        } else {
            prop.getProperty(PROP_LEAK_DETECTION_THRESHOLD_MILLIS).toLong()
        }

        logger.info("+++ Loaded db configuration with DSN " + prop.getProperty(PROP_DB_DSN))
        return HikariCpDbConfiguration(
            prop.getProperty(PROP_DB_DSN),
            prop.getProperty(PROP_DB_USERNAME),
            prop.getProperty(PROP_DB_PASSWORD),
            minPoolSize,
            prop.getProperty(PROP_MAX_POOL_SIZE).toInt(),
            prop.getProperty(PROP_PREP_STMT_CACHE_SIZE).toInt(),
            prop.getProperty(PROP_PREP_STMT_CACHE_SQL_LIMIT).toInt(),
            detectionThresholdMillis,
        )
    }
}