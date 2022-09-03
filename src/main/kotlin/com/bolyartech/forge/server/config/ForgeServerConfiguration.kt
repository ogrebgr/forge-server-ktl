package com.bolyartech.forge.server.config

data class ForgeServerConfiguration(
    val serverLogName: String,
    val staticFilesDir: String,
    val isPathInfoEnabled: Boolean,
    val maxSlashesInPathInfo: Int,
) {
    companion object {
        const val DEFAULT_IS_PATH_INFO_ENABLED = true
        const val DEFAULT_MAX_SLASHES_IN_PATH_INFO = 10
    }

    init {
        if (serverLogName.isEmpty()) {
            throw IllegalArgumentException("serverLogName cannot be empty")
        }

        if (staticFilesDir.isEmpty()) {
            throw IllegalArgumentException("staticFilesDir cannot be empty")
        }

        if (maxSlashesInPathInfo < 0) {
            throw IllegalArgumentException("maxSlashesInPathInfo cannot be negative")
        }
    }
}