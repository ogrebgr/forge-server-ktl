package com.bolyartech.forge.server.route

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.response.HttpHeaders
import com.bolyartech.forge.server.route.RequestContext.ServerData
import com.bolyartech.forge.server.session.Session
import com.bolyartech.forge.server.session.SessionImpl
import com.google.common.base.Strings
import com.google.common.io.CharStreams
import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.Part
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*

class RequestContextImpl(private val httpReq: HttpServletRequest, private val routePath: String) : RequestContext {
    companion object {
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val CONTENT_TYPE_FORM_ENCODED = "application/x-www-form-urlencoded"
        private const val CONTENT_TYPE_MULTIPART = "multipart/form-data"
    }

    private val queryParams: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val postParams: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val cookieParams: MutableMap<String, Cookie> = mutableMapOf()
    private val pathInfoParams: MutableList<String> = mutableListOf()
    private val pathInfoString: String = if (httpReq.pathInfo.length > routePath.length) httpReq.pathInfo.substring(routePath.length) else ""
    private var session: Session? = null
    private var cookiesInitialized = false
    private var isMultipart = false
    private lateinit var serverData: ServerData
    private var body: String? = null

    private var isBodyConsumed = false
    private var areGetParametersExtracted = false
    private var arePostParametersExtracted = false
    private var arePiParametersExtracted = false


    override fun getSession(): Session {
        if (session == null) {
            session = SessionImpl(httpReq.session)
        }
        return session as Session
    }

    override fun getFromQuery(parameterName: String): String? {
        if (!areGetParametersExtracted) {
            extractParameters(httpReq.queryString, queryParams)
            areGetParametersExtracted = true
        }

        val list = queryParams[parameterName]
        return if (list != null && list.size > 0) {
            list[0]
        } else {
            null
        }
    }


    override fun getMultipleFromQuery(parameterName: String): List<String> {
        if (!areGetParametersExtracted) {
            extractParameters(httpReq.queryString, queryParams)
            areGetParametersExtracted = true
        }
        return queryParams[parameterName] ?: emptyList()
    }

    override fun getFromPost(parameterName: String): String? {
        if (!arePostParametersExtracted) {
            try {
                extractPostParameters()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        val list: List<String>? = postParams[parameterName]
        return if (list != null && list.size > 0) {
            list[0]
        } else {
            null
        }
    }

    override fun getMultipleFromPost(parameterName: String): List<String> {
        if (!arePostParametersExtracted) {
            try {
                extractPostParameters()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        return postParams[parameterName] ?: emptyList()
    }

    override fun getPathInfoParameters(): List<String> {
        if (!arePiParametersExtracted) {
            extractPiParameters()
        }
        return pathInfoParams
    }

    override fun getPi(): List<String> {
        return getPathInfoParameters()
    }


    override fun getRoutePath(): String {
        return routePath
    }


    override fun getScheme(): String {
        return httpReq.scheme.lowercase(Locale.getDefault())
    }


    @Throws(IOException::class, ServletException::class)
    override fun getPart(partName: String): Part? {
        check(!arePostParametersExtracted) { "You can either user getFromPost() or getPart() in handling a request but not both" }
        return httpReq.getPart(partName)
    }


    override fun getPathInfoString(): String {
        return pathInfoString
    }


    override fun getCookie(cookieName: String): Cookie? {
        initializeCookies()
        return cookieParams[cookieName]
    }


    override fun optFromQuery(parameterName: String, defaultValue: String): String {
        var ret: String? = getFromQuery(parameterName)
        if (ret == null) {
            ret = defaultValue
        }
        return ret
    }


    override fun optFromPost(parameterName: String, defaultValue: String): String {
        var ret = getFromPost(parameterName)
        if (ret == null) {
            ret = defaultValue
        }
        return ret
    }


    override fun getHeader(headerName: String): String? {
        return httpReq.getHeader(headerName)
    }


    override fun getHeaderValues(headerName: String): List<String> {
        val values = httpReq.getHeaders(headerName)
        return if (values != null) {
            Collections.list(values)
        } else {
            emptyList()
        }
    }


    override fun isMultipart(): Boolean {
        return isMultipart
    }


    override fun getMethod(): HttpMethod {
        return getHttpMethod()
    }

    override fun getHttpMethod(): HttpMethod {
        return when (httpReq.method.lowercase(Locale.getDefault())) {
            "get" -> HttpMethod.GET
            "post" -> HttpMethod.POST
            "put" -> HttpMethod.PUT
            "delete" -> HttpMethod.DELETE
            else -> HttpMethod.UNSUPPORTED
        }
    }

    override fun isMethod(method: HttpMethod): Boolean {
        return httpReq.method.lowercase(Locale.getDefault()) == method.methodName.lowercase()
    }

    override fun getServerData(): ServerData {
        if (!::serverData.isInitialized) {
            serverData = ServerData(
                httpReq.localAddr,
                httpReq.serverName,
                httpReq.protocol,
                httpReq.localPort,
                httpReq.method,
                httpReq.queryString,
                httpReq.getHeader(HttpHeaders.ACCEPT),
                httpReq.getHeader(HttpHeaders.ACCEPT_CHARSET),
                httpReq.getHeader(HttpHeaders.ACCEPT_ENCODING),
                httpReq.getHeader(HttpHeaders.ACCEPT_LANGUAGE),
                httpReq.getHeader(HttpHeaders.CONNECTION),
                httpReq.getHeader(HttpHeaders.HOST),
                httpReq.getHeader(HttpHeaders.REFERRER),
                httpReq.getHeader(HttpHeaders.USER_AGENT),
                httpReq.remoteAddr,
                httpReq.remoteHost,
                httpReq.remotePort,
                httpReq.requestURI,
                httpReq.pathInfo
            )
        }

        return serverData
    }

    override fun getBody(): String? {
        if (!isBodyConsumed) {
            body = try {
                CharStreams.toString(httpReq.reader)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            isBodyConsumed = true
        }
        return body
    }

    override fun getRawQueryString(): String? {
        return httpReq.queryString
    }


    override fun getCookies(): List<Cookie> {
        val ret: MutableList<Cookie> = mutableListOf()
        val cs = httpReq.cookies
        if (cs != null) {
            ret.addAll(Arrays.asList(*cs))
        }
        return ret
    }


    override fun getHeaders(): Map<String, String> {
        val hs: List<String> = Collections.list(httpReq.headerNames)
        val ret: MutableMap<String, String> = HashMap()
        for (h in hs) {
            ret[h] = httpReq.getHeader(h)
        }
        return ret
    }


    private fun extractParameters(queryString: String?, to: MutableMap<String, MutableList<String>>) {
        if (!Strings.isNullOrEmpty(queryString)) {
            try {
                val split = queryString!!.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (aSplit in split) {
                    val keyValue = aSplit.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (keyValue.size == 1) {
                        addParamValue(URLDecoder.decode(keyValue[0], "UTF-8"), "", to)
                    } else {
                        addParamValue(
                            URLDecoder.decode(keyValue[0], "UTF-8"),
                            URLDecoder.decode(keyValue[1], "UTF-8"),
                            to
                        )
                    }
                }
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }
        }
    }

    @Throws(IOException::class)
    private fun extractPostParameters() {
        val body = getBody()
        if (body == null) {
            arePostParametersExtracted = true
            return
        }

        if (httpReq.method.lowercase().equals(HttpMethod.POST.methodName.lowercase())) {
            val contentType = httpReq.getHeader(HEADER_CONTENT_TYPE)
            if (contentType != null) {
                if (contentType.lowercase(Locale.getDefault()).contains(CONTENT_TYPE_FORM_ENCODED.lowercase(Locale.getDefault()))) {
                    extractParameters(body, postParams)
                } else if (contentType.lowercase(Locale.getDefault())
                        .contains(CONTENT_TYPE_MULTIPART.lowercase(Locale.getDefault()))
                ) {
                    isMultipart = true
                }
            }
        }
        arePostParametersExtracted = true
    }

    private fun addParamValue(key: String, value: String, to: MutableMap<String, MutableList<String>>) {
        var tmp = to[key]
        if (tmp == null) {
            tmp = mutableListOf()
            to[key] = tmp
        }
        tmp.add(value)
    }

    private fun extractPiParameters() {
        //protection against directory traversal. Jetty never sends '..' here but other containers may do so...
        check(!pathInfoParams.contains("..")) { "Path info contains '..'" }
        val piRaw = pathInfoString!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (s in piRaw) {
            if (s.trim { it <= ' ' }.isNotEmpty()) {
                pathInfoParams.add(s)
            }
        }
        arePiParametersExtracted = true
    }

    private fun initializeCookies() {
        if (!cookiesInitialized) {
            val cs = httpReq.cookies
            if (cs != null) {
                for (c in cs) {
                    cookieParams[c.name] = c
                }
            }
            cookiesInitialized = true
        }
    }
}