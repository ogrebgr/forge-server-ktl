package com.bolyartech.forge.server.db

import com.bolyartech.forge.server.config.ForgeConfigurationException
import java.lang.Boolean
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.pathString

class DbConfigurationLoaderFile(private val configDirPath: Path) : DbConfigurationLoader {
    companion object {
        private const val FILENAME = "db.conf"
        private const val PROP_DB_DSN = "db_dsn"
        private const val PROP_DB_USERNAME = "db_username"
        private const val PROP_DB_PASSWORD = "db_password"
        private const val PROP_C3P0_MAX_STATEMENTS = "c3p0_max_statements"
        private const val PROP_C3P0_INITIAL_POOL_SIZE = "c3p0_initial_pool_size"
        private const val PROP_C3P0_MIN_POOL_SIZE = "c3p0_min_pool_size"
        private const val PROP_C3P0_MAX_POOL_SIZE = "c3p0_max_pool_size"
        private const val PROP_C3P0_IDLE_CONNECTION_TEST_PERIOD = "c3p0_idle_connection_test_period"
        private const val PROP_C3P0_TEST_CONNECTION_ON_CHECK_IN = "c3p0_test_connection_on_check_in"
        private const val PROP_C3P0_TEST_CONNECTION_ON_CHECKOUT = "c3p0_test_connection_on_checkout"
    }

    override fun load(): DbConfiguration {
        val path = Path.of(configDirPath.pathString, FILENAME)
        if (!path.exists()) {
            throw ForgeConfigurationException("Cannot find (${path.pathString})")
        }
        val prop = Properties()
        Files.newInputStream(path).use {
            prop.load(it)
        }

        return DbConfiguration(
            prop.getProperty(PROP_DB_DSN),
            prop.getProperty(PROP_DB_USERNAME),
            prop.getProperty(PROP_DB_PASSWORD),
            prop.getProperty(PROP_C3P0_MAX_STATEMENTS).toInt(),
            prop.getProperty(PROP_C3P0_INITIAL_POOL_SIZE).toInt(),
            prop.getProperty(PROP_C3P0_MIN_POOL_SIZE).toInt(),
            prop.getProperty(PROP_C3P0_MAX_POOL_SIZE).toInt(),
            prop.getProperty(PROP_C3P0_IDLE_CONNECTION_TEST_PERIOD).toInt(),
            Boolean.parseBoolean(prop.getProperty(PROP_C3P0_TEST_CONNECTION_ON_CHECK_IN)),
            Boolean.parseBoolean(prop.getProperty(PROP_C3P0_TEST_CONNECTION_ON_CHECKOUT))
        )
    }
}