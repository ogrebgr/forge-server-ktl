package com.bolyartech.forge.server.config

import java.io.File
import java.util.*

data class ForgeServerConfiguration(
    val serverNames: List<String>,
    val logPrefix: String,
    val staticFilesDir: String,
    val isPathInfoEnabled: Boolean,
    val maxSlashesInPathInfo: Int,
    val uploadsDirectory: String,
    val downloadsDirectory: String,
) {
    companion object {
        const val DEFAULT_IS_PATH_INFO_ENABLED = true
        const val DEFAULT_MAX_SLASHES_IN_PATH_INFO = 10

        fun extractIntValue(prop: Properties, propertyName: String): Int {
            val tmp = prop.getProperty(propertyName) ?: throw ForgeConfigurationException("$propertyName is missing/empty")

            return try {
                tmp.toInt()
            } catch (e: NumberFormatException) {
                throw ForgeConfigurationException("$propertyName is not integer")
            }
        }

        fun extractIntValuePositive(prop: Properties, propertyName: String): Int {
            val tmp = extractIntValue(prop, propertyName)
            if (tmp <= 0) {
                throw ForgeConfigurationException("$propertyName is not positive integer")
            }

            return tmp
        }

        fun extractIntValue0Positive(prop: Properties, propertyName: String): Int {
            val tmp = extractIntValue(prop, propertyName)
            if (tmp < 0) {
                throw ForgeConfigurationException("$propertyName is not positive integer or 0")
            }

            return tmp
        }

        fun extractStringValue(prop: Properties, propertyName: String): String {
            val tmp = prop.getProperty(propertyName) ?: throw ForgeConfigurationException("$propertyName is missing/empty")

            return tmp
        }

        fun extractBooleanValue(prop: Properties, propertyName: String): Boolean {
            val tmp = prop.getProperty(propertyName) ?: throw ForgeConfigurationException("$propertyName is missing/empty")

            return tmp.toBoolean()
        }
    }

    init {
        if (logPrefix.isEmpty()) {
            throw IllegalArgumentException("serverLogName cannot be empty")
        }

        if (staticFilesDir.isEmpty()) {
            throw IllegalArgumentException("staticFilesDir cannot be empty")
        }

        if (staticFilesDir.endsWith(File.separator)) {
            throw IllegalArgumentException("staticFilesDir must NOT end with ${File.separator}")
        }

        if (maxSlashesInPathInfo < 0) {
            throw IllegalArgumentException("maxSlashesInPathInfo cannot be negative")
        }
    }
}