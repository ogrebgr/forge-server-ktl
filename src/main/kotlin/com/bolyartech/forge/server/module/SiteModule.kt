package com.bolyartech.forge.server.module

import com.bolyartech.forge.server.route.Route

interface SiteModule {
    /**
     * In this method modules should create their routes and return them
     *
     * @return routes of the module
     */
    fun createRoutes(): List<Route>

    /**
     * Unique module name
     *
     *
     * Module names must be lowercase, contain only letter, numbers, dot or hyphen
     *
     * @return system name of the module
     */
    fun getSystemName(): String

    /**
     * Returns short description of the module
     *
     * @return Short description of the module
     */
    fun getShortDescription(): String

    /**
     * Version code of the module
     *
     *
     * Version codes start from 1 and are changed in each release. There must not be two releases with the same
     * version code. Version codes are usually used not by humans but by module management systems.
     *
     * @return version code
     */
    fun getVersionCode(): Int

    /**
     * Version name is version information which is meant to be used by humans like v3.2.1alpha
     *
     * @return version name
     */
    fun getVersionName(): String
}
