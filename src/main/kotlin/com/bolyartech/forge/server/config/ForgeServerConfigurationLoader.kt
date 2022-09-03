package com.bolyartech.forge.server.config

interface ForgeServerConfigurationLoader {
    @Throws(ForgeConfigurationException::class)
    fun load(): ForgeServerConfiguration
}