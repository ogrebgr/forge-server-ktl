package com.bolyartech.forge.server.db

import com.bolyartech.forge.server.config.ForgeConfigurationException

interface HikariCpDbConfigurationLoader {
    @Throws(ForgeConfigurationException::class)
    fun load(): HikariCpDbConfiguration
}