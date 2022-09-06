package com.bolyartech.forge.server.config

import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.annotation.Nonnull
import kotlin.io.path.exists
import kotlin.io.path.pathString

class ForgeServerConfigurationLoaderFile(private val configDirPath: Path) : ForgeServerConfigurationLoader {
    companion object {
        const val FORGE_CONF_FILENAME = "forge.conf"
        private const val PROP_SERVER_LOG_NAME = "server_log_name"
        private const val PROP_STATIC_FILES_DIR = "static_files_dir"
        private const val PROP_PATH_INFO_ENABLED = "path_info_enabled"
        private const val PROP_MAX_SLASHES_IN_PATH_INFO = "max_slashes_in_path_info"
        private const val PROP_UPLOADS_DIR = "uploads_dir"
        private const val PROP_DOWNLOADS_DIR = "downloads_dir"
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Throws(ForgeConfigurationException::class)
    override fun load(): ForgeServerConfiguration {
        val path = Path.of(configDirPath.pathString, FORGE_CONF_FILENAME)
        if (!path.exists()) {
            throw ForgeConfigurationException("Cannot find forge configuration file (${path.pathString})")
        }

        val prop = Properties()
        Files.newInputStream(path).use {
            prop.load(it)
        }

        val logName = prop.getProperty(PROP_SERVER_LOG_NAME)
        if (logName == null) {
            logger.error(
                "Missing {} in forge.conf",
                PROP_SERVER_LOG_NAME
            )
        }

        val staticDir = prop.getProperty(PROP_STATIC_FILES_DIR)
        if (staticDir == null) {
            logger.error(
                "Missing {} in forge.conf",
                PROP_STATIC_FILES_DIR
            )
        }

        if (logName == null || staticDir == null) {
            throw ForgeConfigurationException("Missing properties")
        }

        val isPathInfoEnabledRaw =
            prop.getProperty(PROP_PATH_INFO_ENABLED)
        var isPathInfoEnabled: Boolean = ForgeServerConfiguration.DEFAULT_IS_PATH_INFO_ENABLED
        if (isPathInfoEnabledRaw != null) {
            val tmp = isPathInfoEnabledRaw.trim { it <= ' ' }.lowercase(Locale.getDefault())
            isPathInfoEnabled = tmp == "true" || tmp == "1"
        }

        val maxSlashesRaw =
            prop.getProperty(PROP_MAX_SLASHES_IN_PATH_INFO)

        var maxSlashes: Int = ForgeServerConfiguration.DEFAULT_MAX_SLASHES_IN_PATH_INFO
        try {
            maxSlashes = maxSlashesRaw.toInt()
        } catch (e: NumberFormatException) {
            logger.error(
                "Invalid value for {}. Must be integer",
                PROP_MAX_SLASHES_IN_PATH_INFO
            )
        }

        val uploadsDir = prop.getProperty(PROP_UPLOADS_DIR)

        val downloadsDir = prop.getProperty(PROP_DOWNLOADS_DIR)

        return ForgeServerConfiguration(
            logName,
            staticDir,
            isPathInfoEnabled,
            maxSlashes,
            normalizePath(uploadsDir),
            normalizePath(downloadsDir)
        )
    }

    private fun normalizePath(@Nonnull path: String): String {
        var pathTmp = path.lowercase(Locale.getDefault())

        if (path.length > 1) {
            if (path.endsWith("/")) {
                pathTmp = path.substring(0, path.length - 1)
            }
        }

        return pathTmp
    }
}