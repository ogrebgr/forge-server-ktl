package com.bolyartech.forge.server.module

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.route.Route

interface SiteModuleRegister {
    /**
     * Registers a module
     *
     * @param mod module to be registered
     */
    fun registerModule(mod: SiteModule)

    /**
     * Checks if module is registered
     *
     * @param mod module
     * @return true if module is registered, false otherwise
     */
    fun isModuleRegistered(mod: SiteModule): Boolean

    /**
     * Matches [HttpMethod] and path to a route in the registered modules
     *
     * @param method HTTP method like GET, POST, etc.
     * @param path   Path to be matched
     * @return Route with matched method and path or null if no route is matched
     */
    fun match(method: HttpMethod, path: String): Route?
}