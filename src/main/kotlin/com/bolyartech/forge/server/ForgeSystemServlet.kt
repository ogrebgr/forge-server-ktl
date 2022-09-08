package com.bolyartech.forge.server

import com.bolyartech.forge.server.handler.RouteHandler
import com.bolyartech.forge.server.module.SiteModule
import com.bolyartech.forge.server.module.SiteModuleRegister
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.ResponseException
import com.bolyartech.forge.server.route.InvalidParameterValueException
import com.bolyartech.forge.server.route.MissingParameterValueException
import com.bolyartech.forge.server.route.RequestContextImpl
import com.bolyartech.forge.server.route.Route
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.inject.Inject


class ForgeSystemServlet @Inject constructor(
    private val serverNames: List<String>,
    modules: List<SiteModule>,
    private val siteModuleRegister: SiteModuleRegister,
    private val notFoundHandler: RouteHandler? = null,
    private val internalServerErrorHandler: RouteHandler? = null,
) : HttpServlet() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        if (modules.isNotEmpty()) {
            for (mod in modules) {
                siteModuleRegister.registerModule(mod)
            }
            logger.info("Server initialized and started.")
        } else {
            logger.error("getModules() returned empty list of modules, so no endpoints are registered.")
        }
    }


    @Throws(IOException::class)
    private fun processRequest(req: HttpServletRequest, httpResp: HttpServletResponse) {
        if (serverNames.isNotEmpty()) {
            if (req.serverName !in serverNames) {
                notFound(req, httpResp)
                return
            }
        }
        try {
            val method = HttpMethod.valueOf(req.method)
            val route = siteModuleRegister.match(method, req.pathInfo)
            route?.let { handle(req, httpResp, it) } ?: notFound(req, httpResp)
        } catch (e: IllegalArgumentException) {
            notFound(req, httpResp)
        }
    }

    @Throws(IOException::class)
    private fun handle(req: HttpServletRequest, httpResp: HttpServletResponse, route: Route) {
        try {
            route.handle(req, httpResp)
        } catch (e: ResponseException) {
            if (e.cause is MissingParameterValueException) {
                logger.warn("Missing parameter(s) in \"{}\": {}", route.getPath(), e.cause!!.message)
                badRequest(httpResp)
                return
            }
            if (e.cause is InvalidParameterValueException) {
                logger.warn("Invalid parameter value in \"{}\": {}", route.getPath(), e.cause!!.message)
                badRequest(httpResp)
                return
            }
            logger.error("Error handling {}, Error: {}", route, e.message)
            logger.error("Exception: ", e)
            if (internalServerErrorHandler == null) {
                stockInternalServerError(httpResp)
            } else {
                val resp = internalServerErrorHandler.handle(RequestContextImpl(req, req.pathInfo))
                resp.toServletResponse(httpResp)
            }
        }
    }

    @Throws(IOException::class)
    private fun notFound(req: HttpServletRequest, httpResp: HttpServletResponse) {
        if (notFoundHandler != null) {
            val resp: Response = notFoundHandler.handle(RequestContextImpl(req, req.pathInfo))
            resp.toServletResponse(httpResp)
        } else {
            stockNotFound(req, httpResp)
        }
    }


    @Throws(IOException::class)
    private fun stockNotFound(req: HttpServletRequest, httpResp: HttpServletResponse) {
        httpResp.status = HttpServletResponse.SC_NOT_FOUND
        val pw = httpResp.writer
        pw.print("404 Not found")
        pw.flush()
        pw.close()
    }


    private fun stockInternalServerError(httpResp: HttpServletResponse) {
        httpResp.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        try {
            val pw = httpResp.writer
            pw.print("500 Internal server error")
            pw.flush()
            pw.close()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }


    private fun badRequest(httpResp: HttpServletResponse) {
        httpResp.status = HttpServletResponse.SC_BAD_REQUEST
        try {
            val pw = httpResp.writer
            pw.print("Bad request")
            pw.flush()
            pw.close()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    @Throws(IOException::class)
    override fun doGet(req: HttpServletRequest, httpResp: HttpServletResponse) {
        processRequest(req, httpResp)
    }

    @Throws(IOException::class)
    override fun doPost(req: HttpServletRequest, httpResp: HttpServletResponse) {
        processRequest(req, httpResp)
    }

    @Throws(IOException::class)
    override fun doPut(req: HttpServletRequest, httpResp: HttpServletResponse) {
        processRequest(req, httpResp)
    }

    @Throws(IOException::class)
    override fun doDelete(req: HttpServletRequest, httpResp: HttpServletResponse) {
        processRequest(req, httpResp)
    }
}