package com.bolyartech.forge.server.route

import com.bolyartech.forge.server.HttpMethod
import org.slf4j.LoggerFactory
import javax.annotation.Nonnull


class RouteRegisterImpl(isPathInfoEnabled: Boolean, maxPathSegments: Int) : RouteRegister {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val endpointsGetExact: MutableMap<String, RouteRegister.Registration> = mutableMapOf()
    private val endpointsGetStartsWith: MutableList<RouteRegister.Registration> = mutableListOf()
    private val endpointsGetRuntimeResolved: MutableMap<String, RouteRegister.Registration> = mutableMapOf()

    private val endpointsPostExact: MutableMap<String, RouteRegister.Registration> = mutableMapOf()
    private val endpointsPostStartsWith: MutableList<RouteRegister.Registration> = mutableListOf()
    private val endpointsPostRuntimeResolved: MutableMap<String, RouteRegister.Registration> = mutableMapOf()

    private val endpointsDeleteExact: MutableMap<String, RouteRegister.Registration> = mutableMapOf()
    private val endpointsDeleteStartsWith: MutableList<RouteRegister.Registration> = mutableListOf()
    private val endpointsDeleteRuntimeResolved: MutableMap<String, RouteRegister.Registration> = mutableMapOf()

    private val endpointsPutExact: MutableMap<String, RouteRegister.Registration> = mutableMapOf()
    private val endpointsPutStartsWith: MutableList<RouteRegister.Registration> = mutableListOf()
    private val endpointsPutRuntimeResolved: MutableMap<String, RouteRegister.Registration> = mutableMapOf()


    override fun register(moduleName: String, route: Route) {
        when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                when (route) {
                    is RouteStartsWith -> {
                        registerStartsWith(endpointsGetStartsWith, moduleName, route)
                    }

                    is RouteExact -> registerActual(endpointsGetExact, moduleName, route)
                    is RouteRuntimeResolved -> {
                        if (!route.getPath().endsWith("/")) {
                            logger.warn("RouteRuntimeResolved GET ${route.getPath()} not ending with a slash (/). Will point to single file (if exist)")
                        }
                        registerActual(endpointsGetRuntimeResolved, moduleName, route)
                    }

                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            HttpMethod.POST -> {
                when (route) {
                    is RouteStartsWith -> {
                        registerStartsWith(endpointsPostStartsWith, moduleName, route)
                    }

                    is RouteExact -> registerActual(endpointsPostExact, moduleName, route)
                    is RouteRuntimeResolved -> {
                        if (!route.getPath().endsWith("/")) {
                            logger.warn("RouteRuntimeResolved POST ${route.getPath()} not ending with a slash (/). Will point to single file (if exist)")
                        }

                        registerActual(endpointsPostRuntimeResolved, moduleName, route)
                    }

                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            HttpMethod.PUT -> {
                when (route) {
                    is RouteStartsWith -> {
                        registerStartsWith(endpointsPutStartsWith, moduleName, route)
                    }

                    is RouteExact -> registerActual(endpointsPutExact, moduleName, route)
                    is RouteRuntimeResolved -> {
                        if (!route.getPath().endsWith("/")) {
                            logger.warn("RouteRuntimeResolved PUT ${route.getPath()} not ending with a slash (/). Will point to single file (if exist)")
                        }
                        registerActual(endpointsPutRuntimeResolved, moduleName, route)
                    }

                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            HttpMethod.DELETE -> {
                when (route) {
                    is RouteStartsWith -> {
                        registerStartsWith(endpointsDeleteStartsWith, moduleName, route)
                    }

                    is RouteExact -> registerActual(endpointsDeleteExact, moduleName, route)
                    is RouteRuntimeResolved -> {
                        if (!route.getPath().endsWith("/")) {
                            logger.warn("RouteRuntimeResolved DELETE ${route.getPath()} not ending with a slash (/). Will point to single file (if exist)")
                        }
                        registerActual(endpointsDeleteRuntimeResolved, moduleName, route)
                    }

                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            else -> throw IllegalArgumentException("Cannot register unsupported HttpMethod")
        }
    }

    private fun registerStartsWith(
        endpointsStartsWith: MutableList<RouteRegister.Registration>,
        moduleName: String,
        route: RouteStartsWith
    ) {
        if (!route.getPath().endsWith("/")) {
            throw RouteRegisterExceptionBadPathFormat("*StartsWith routes must end with a dash (/): ${route.getPath()} (${route.routeHandler::class.java})")
        }

        endpointsStartsWith.forEach {
            if (it.route.getPath() == route.getPath()) {
                throw RouteRegisterExceptionAlreadyRegistered(route.getPath())
            }
        }

        endpointsStartsWith.add(RouteRegister.Registration(moduleName, route))

        endpointsStartsWith.sortWith { r1: RouteRegister.Registration, r2: RouteRegister.Registration ->
            r2.route.getPath().length - r1.route.getPath().length
        }
    }

    private fun registerActual(
        endpoints: MutableMap<String, RouteRegister.Registration>,
        moduleName: String,
        route: Route
    ) {
        val warn = endpoints.containsKey(route.getPath())
        val wildcard = if (route is RouteRuntimeResolved) {
            "*"
        } else {
            ""
        }

        endpoints[route.getPath()] = RouteRegister.Registration(moduleName, route)
        if (!warn) {
            logger.info("Registered route ${route.getHttpMethod()} ${route.getPath()}$wildcard (${route.getHandler()::class.simpleName})")
        } else {
            throw RouteRegisterExceptionAlreadyRegistered(route.getPath())
        }
    }

    private fun isRegisteredStartsWith(endpoints: MutableList<RouteRegister.Registration>, route: Route): Boolean {
        endpoints.forEach {
            if (it.route.getPath() == route.getPath()) {
                return true
            }
        }

        return false
    }

    override fun isRegistered(route: Route): Boolean {
        return when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                when (route) {
                    is RouteStartsWith -> isRegisteredStartsWith(endpointsGetStartsWith, route)
                    is RouteExact -> endpointsGetExact.containsKey(route.getPath())
                    is RouteRuntimeResolved -> endpointsGetRuntimeResolved.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            HttpMethod.POST -> {
                when (route) {
                    is RouteStartsWith -> isRegisteredStartsWith(endpointsPostStartsWith, route)
                    is RouteExact -> endpointsPostExact.containsKey(route.getPath())
                    is RouteRuntimeResolved -> endpointsPostRuntimeResolved.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            HttpMethod.PUT -> {
                when (route) {
                    is RouteStartsWith -> isRegisteredStartsWith(endpointsPutStartsWith, route)
                    is RouteExact -> endpointsPutExact.containsKey(route.getPath())
                    is RouteRuntimeResolved -> endpointsPutRuntimeResolved.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            HttpMethod.DELETE -> {
                when (route) {
                    is RouteStartsWith -> isRegisteredStartsWith(endpointsDeleteStartsWith, route)
                    is RouteExact -> endpointsDeleteExact.containsKey(route.getPath())
                    is RouteRuntimeResolved -> endpointsDeleteRuntimeResolved.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }

            else -> {
                throw IllegalArgumentException("route's method (route.getHttpMethod()) is unsupported")
            }
        }
    }

    override fun getRegistration(@Nonnull route: Route): RouteRegister.Registration? {
        return when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                when (route) {
                    is RouteStartsWith -> getRegistrationStartsWith(endpointsGetStartsWith, route.getPath())
                    is RouteExact -> endpointsGetExact[route.getPath()]
                    is RouteRuntimeResolved -> endpointsGetRuntimeResolved[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        null
                    }
                }
            }

            HttpMethod.POST -> {
                when (route) {
                    is RouteExact -> endpointsPostExact[route.getPath()]
                    is RouteRuntimeResolved -> endpointsPostRuntimeResolved[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        null
                    }
                }
            }

            HttpMethod.PUT -> {
                when (route) {
                    is RouteExact -> endpointsPutExact[route.getPath()]
                    is RouteRuntimeResolved -> endpointsPutRuntimeResolved[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        null
                    }
                }
            }

            HttpMethod.DELETE -> {
                when (route) {
                    is RouteExact -> endpointsDeleteExact[route.getPath()]
                    is RouteRuntimeResolved -> endpointsDeleteRuntimeResolved[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        null
                    }
                }
            }

            else -> {
                logger.warn("route's method (route.getHttpMethod()) is unsupported")
                null
            }
        }
    }

    private fun getRegistrationStartsWith(
        endpointsGetStartsWith: MutableList<RouteRegister.Registration>,
        path: String
    ): RouteRegister.Registration? {
        endpointsGetStartsWith.forEach {
            if (it.route.getPath() == path) {
                return it
            }
        }

        return null
    }

    override fun match(method: HttpMethod, path: String): Route? {
        return when (method) {
            HttpMethod.GET -> {
                var tmp = match(endpointsGetExact, path)
                if (tmp == null) {
                    tmp = match(endpointsGetRuntimeResolved, path)
                }
                if (tmp == null) {
                    tmp = match(endpointsGetStartsWith, path)
                }
                tmp
            }

            HttpMethod.POST -> {
                var tmp = match(endpointsPostExact, path)
                if (tmp == null) {
                    tmp = match(endpointsPostRuntimeResolved, path)
                }
                if (tmp == null) {
                    tmp = match(endpointsPostStartsWith, path)
                }
                tmp
            }

            HttpMethod.PUT -> {
                var tmp = match(endpointsPutExact, path)
                if (tmp == null) {
                    tmp = match(endpointsPutRuntimeResolved, path)
                }
                if (tmp == null) {
                    tmp = match(endpointsPutStartsWith, path)
                }
                tmp
            }

            HttpMethod.DELETE -> {
                var tmp = match(endpointsDeleteExact, path)
                if (tmp == null) {
                    tmp = match(endpointsDeleteRuntimeResolved, path)
                }
                if (tmp == null) {
                    tmp = match(endpointsDeleteStartsWith, path)
                }
                tmp
            }

            else -> {
                throw IllegalArgumentException("route's method (route.getHttpMethod()) is unsupported")
            }
        }
    }

    private fun match(endpoints: Map<String, RouteRegister.Registration>, path: String): Route? {
        endpoints.entries.forEach {
            if (it.value.route.isMatching(path)) {
                return it.value.route
            }
        }

        return null
    }

    private fun match(endpoints: List<RouteRegister.Registration>, path: String): Route? {
        endpoints.forEach {
            if (it.route.isMatching(path)) {
                return it.route
            }
        }

        return null
    }

    internal fun getEndpointsGetStartsWith(): List<RouteRegister.Registration> {
        return endpointsGetStartsWith
    }
}

sealed class RouteRegisterException(message: String) : Exception(message)
class RouteRegisterExceptionBadPathFormat(msg: String) : RouteRegisterException("Bad path format. $msg")
class RouteRegisterExceptionAlreadyRegistered(path: String) : RouteRegisterException("Path already registered. $path")