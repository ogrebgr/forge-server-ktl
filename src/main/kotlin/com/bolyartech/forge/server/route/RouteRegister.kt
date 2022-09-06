package com.bolyartech.forge.server.route

import com.bolyartech.forge.server.HttpMethod

interface RouteRegister {
    /**
     * Registers a route for "dynamic" endpoints, i.e. non-static files
     *
     * @param moduleSystemName Module system name
     * @param route            Route
     * @see .registerStatics
     */
    @Throws(RouteRegisterException::class)
    fun register(moduleName: String, route: Route)

    /**
     * Checks if a route is registered
     *
     * @param route Route
     * @return true if route is registered, false otherwise
     */
    fun isRegistered(route: Route): Boolean

    /**
     * Returns route registration
     *
     * @param route Route object
     * @return Registration of the route
     */
    fun getRegistration(route: Route): RouteRegister.Registration?

    /**
     * Matches Route against HTTP method and URL path
     *
     * @param method HTTP method
     * @param path   URL Path
     * @return matched route or null if no route is matched
     */
    fun match(method: HttpMethod, path: String): Route?

    data class Registration(val moduleName: String, val route: Route)
}