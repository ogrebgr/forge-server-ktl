package com.bolyartech.forge.server.route

import com.bolyartech.forge.server.HttpMethod
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.Nonnull


class RouteRegisterImpl(isPathInfoEnabled: Boolean, maxPathSegments: Int) : RouteRegister {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val endpointsGetExact: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()
    private val endpointsGetFlexible: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()

    private val endpointsPostExact: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()
    private val endpointsPostFlexible: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()

    private val endpointsDeleteExact: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()
    private val endpointsDeleteFlexible: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()

    private val endpointsPutExact: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()
    private val endpointsPutFlexible: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()


    override fun register(moduleName: String, route: Route) {
        when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                when (route) {
                    is RouteExact -> registerActual(endpointsGetExact, moduleName, route)
                    is RouteFlexible -> registerActual(endpointsGetFlexible, moduleName, route)
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.POST -> {
                when (route) {
                    is RouteExact -> registerActual(endpointsPostExact, moduleName, route)
                    is RouteFlexible -> registerActual(endpointsPostFlexible, moduleName, route)
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.PUT -> {
                when (route) {
                    is RouteExact -> registerActual(endpointsPutExact, moduleName, route)
                    is RouteFlexible -> registerActual(endpointsPutFlexible, moduleName, route)
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.DELETE -> {
                when (route) {
                    is RouteExact -> registerActual(endpointsDeleteExact, moduleName, route)
                    is RouteFlexible -> registerActual(endpointsDeleteFlexible, moduleName, route)
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            else -> throw IllegalArgumentException("Cannot register unsupported HttpMethod")
        }
    }

    private fun registerActual(
        endpoints: MutableMap<String, RouteRegister.Registration>,
        moduleName: String,
        route: Route
    ) {
        val warn = endpoints.containsKey(route.getPath())
        val wildcard = if (route is RouteFlexible) {
            "*"
        } else {
            ""
        }

        endpoints[route.getPath()] = RouteRegister.Registration(moduleName, route)
        if (!warn) {
            logger.info("Registered route ${route.getHttpMethod()} ${route.getPath()}$wildcard (${route.getHandler()::class.simpleName})")
        } else {
            logger.info("Registered route ${route.getHttpMethod()} ${route.getPath()}$wildcard (${route.getHandler()::class.simpleName}) - method + path already registered. Care.")
        }
    }

    override fun isRegistered(@Nonnull route: Route): Boolean {
        return when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                when (route) {
                    is RouteExact -> endpointsGetExact.containsKey(route.getPath())
                    is RouteFlexible -> endpointsGetFlexible.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.POST -> {
                when (route) {
                    is RouteExact -> endpointsPostExact.containsKey(route.getPath())
                    is RouteFlexible -> endpointsPostFlexible.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.PUT -> {
                when (route) {
                    is RouteExact -> endpointsPutExact.containsKey(route.getPath())
                    is RouteFlexible -> endpointsPutFlexible.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.DELETE -> {
                when (route) {
                    is RouteExact -> endpointsDeleteExact.containsKey(route.getPath())
                    is RouteFlexible -> endpointsDeleteFlexible.containsKey(route.getPath())
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
                return when (route) {
                    is RouteExact -> endpointsGetExact[route.getPath()]
                    is RouteFlexible -> endpointsGetFlexible[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        return null
                    }
                }
            }
            HttpMethod.POST -> {
                return when (route) {
                    is RouteExact -> endpointsPostExact[route.getPath()]
                    is RouteFlexible -> endpointsPostFlexible[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        return null
                    }
                }
            }
            HttpMethod.PUT -> {
                return when (route) {
                    is RouteExact -> endpointsPutExact[route.getPath()]
                    is RouteFlexible -> endpointsPutFlexible[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        return null
                    }
                }
            }
            HttpMethod.DELETE -> {
                return when (route) {
                    is RouteExact -> endpointsDeleteExact[route.getPath()]
                    is RouteFlexible -> endpointsDeleteFlexible[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        return null
                    }
                }
            }
            else -> {
                logger.warn("route's method (route.getHttpMethod()) is unsupported")
                return null
            }
        }
    }

    override fun match(method: HttpMethod, path: String): Route? {
        val pathNorm = normalizePath(path)

        return when (method) {
            HttpMethod.GET -> {
                var tmp = match(endpointsGetExact, pathNorm)
                if (tmp == null) {
                    tmp = match(endpointsGetFlexible, pathNorm)
                }
                tmp
            }
            HttpMethod.POST -> {
                var tmp = match(endpointsPostExact, pathNorm)
                if (tmp == null) {
                    tmp = match(endpointsPostFlexible, pathNorm)
                }
                tmp
            }
            HttpMethod.PUT -> {
                var tmp = match(endpointsPutExact, pathNorm)
                if (tmp == null) {
                    tmp = match(endpointsPutFlexible, pathNorm)
                }
                tmp
            }
            HttpMethod.DELETE -> {
                var tmp = match(endpointsDeleteExact, pathNorm)
                if (tmp == null) {
                    tmp = match(endpointsDeleteFlexible, pathNorm)
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


    companion object {
        fun normalizePath(@Nonnull path: String): String {
            var pathTmp = path.lowercase(Locale.getDefault())

            if (path.length > 1) {
                if (path.endsWith("/")) {
                    pathTmp = path.substring(0, path.length - 1)
                }
            }

            return pathTmp
        }
    }
}