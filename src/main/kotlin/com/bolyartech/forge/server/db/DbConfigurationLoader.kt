package com.bolyartech.forge.server.db

import com.bolyartech.forge.server.config.ForgeConfigurationException

interface DbConfigurationLoader {
    @Throws(ForgeConfigurationException::class)
    fun load(): DbConfiguration
}