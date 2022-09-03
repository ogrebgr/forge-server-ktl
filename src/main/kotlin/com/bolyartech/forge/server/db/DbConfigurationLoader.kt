package com.bolyartech.forge.server.db

interface DbConfigurationLoader {
    fun load(): DbConfiguration
}