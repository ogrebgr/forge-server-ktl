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

    private val endpointsPost: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()
    private val endpointsDelete: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()
    private val endpointsPut: MutableMap<String, RouteRegister.Registration> = ConcurrentHashMap()


    override fun register(moduleName: String, route: Route) {
        when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                when (route) {
                    is RouteSimple -> registerActual(endpointsGetExact, moduleName, route)
                    is RouteFlexible -> registerActual(endpointsGetFlexible, moduleName, route)
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.POST -> registerActual(endpointsPost, moduleName, route)
            HttpMethod.PUT -> registerActual(endpointsPut, moduleName, route)
            HttpMethod.DELETE -> registerActual(endpointsDelete, moduleName, route)
            else -> throw IllegalArgumentException("Cannot register unsupported HttpMethod")
        }
    }

    private fun registerActual(
        endpoints: MutableMap<String, RouteRegister.Registration>,
        moduleName: String,
        route: Route
    ) {
        if (!endpoints.containsKey(route.getPath())) {
            endpoints[route.getPath()] = RouteRegister.Registration(moduleName, route)
            logger.info("Registered route {} {}", route.getHttpMethod(), route.getPath())
        } else {
            throw IllegalStateException("Registered path already exist: " + route.getPath())
        }
    }

    override fun isRegistered(@Nonnull route: Route): Boolean {
        return when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                when (route) {
                    is RouteSimple -> endpointsGetExact.containsKey(route.getPath())
                    is RouteFlexible -> endpointsGetFlexible.containsKey(route.getPath())
                    else -> {
                        throw IllegalArgumentException("route is of unsupported class {${route.javaClass}}")
                    }
                }
            }
            HttpMethod.POST -> endpointsPost.containsKey(route.getPath())
            HttpMethod.PUT -> endpointsPut.containsKey(route.getPath())
            HttpMethod.DELETE -> endpointsDelete.containsKey(route.getPath())
            else -> {
                throw IllegalArgumentException("route's method (route.getHttpMethod()) is unsupported")
            }
        }
    }

    override fun getRegistration(@Nonnull route: Route): RouteRegister.Registration? {
        return when (route.getHttpMethod()) {
            HttpMethod.GET -> {
                return when (route) {
                    is RouteSimple -> endpointsGetExact[route.getPath()]
                    is RouteFlexible -> endpointsGetFlexible[route.getPath()]
                    else -> {
                        logger.warn("route is of unsupported class {${route.javaClass}}")
                        return null
                    }
                }
            }
            HttpMethod.POST -> endpointsPost[route.getPath()]
            HttpMethod.PUT -> endpointsPut[route.getPath()]
            HttpMethod.DELETE -> endpointsDelete[route.getPath()]
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
            HttpMethod.POST -> match(endpointsPost, pathNorm)
            HttpMethod.PUT -> match(endpointsPut, pathNorm)
            HttpMethod.DELETE -> match(endpointsDelete, pathNorm)
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