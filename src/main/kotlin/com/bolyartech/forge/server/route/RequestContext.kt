package com.bolyartech.forge.server.route

import com.bolyartech.forge.server.HttpMethod
import com.bolyartech.forge.server.session.Session
import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.Part
import java.io.IOException

interface RequestContext {
    /**
     * Returns current session.
     * Implementation may decide to initialize the session lazily when this method is called in order to prevent overhead
     *
     * @return Session
     */
    fun getSession(): Session

    /**
     * Returns the value of a query parameter as a String, or null if the parameter does not exist. Alias of [.getFromGet]
     *
     * @param parameterName Parameter name
     * @return Parameter value
     */
    fun getFromQuery(parameterName: String): String?

    /**
     * Returns multi value of a query parameter as a List<String>, or null if the parameter does not exist.
     * Multi-values happen for example when several checkboxes with same name and different values are present in the form
     *
     * @param parameterName Parameter name
     * @return List with values (if any)
     */
    fun getMultipleFromQuery(parameterName: String): List<String>

    /**
     * Returns the value of a POST parameter as a String, or null if the parameter does not exist.
     *
     * @param parameterName Parameter name
     * @return Parameter value
     */
    fun getFromPost(parameterName: String): String?

    /**
     * Returns multi value of a query parameter as a List<String>, or null if the parameter does not exist.
     * Multi-values happen for example when several checkboxes with same name and different values are present in the form
     *
     * @param parameterName Parameter name
     * @return List with values (if any)
     */
    fun getMultipleFromPost(parameterName: String): List<String>

    /**
     * @return List containing path info parameters ordered from left to right
     */
    fun getPathInfoParameters(): List<String>

    /**
     * Alias of [.getPathInfoParameters]
     *
     * @return List containing path info parameters ordered from left to right
     */
    fun getPi(): List<String>

    /**
     * Returns the path of the matched route
     *
     * @return path of the matched route
     */
    fun getRoutePath(): String

    /**
     * Returns HTTP scheme, i.e. http or https (in lowercase)
     *
     * @return HTTP scheme, i.e. http or https (in lowercase)
     */
    fun getScheme(): String

    /**
     * For multipart request this method returns a request part
     *
     * @param partName Part name
     * @return request part
     * @throws IOException      if there is problem retrieving the content
     * @throws ServletException if there is problem retrieving the content
     */
    @Throws(IOException::class, ServletException::class)
    fun getPart(partName: String): Part?

    /**
     * Returns the Path info string, i.e. the path part after the route path
     * For example if we have a request URL `http://somedomain.com/route/path/path/info/string` and
     * route path '/route/path/path' the returned string will be  /path/info/string
     *
     * @return String containing the path info
     */
    fun getPathInfoString(): String

    /**
     * Returns Cookie
     *
     * @param cookieName Cookie name
     * @return Cookie
     */
    fun getCookie(cookieName: String): Cookie?

    /**
     * Optionally gets from query parameters if parameter is present. Alias of [.optFromGet]
     *
     * @param parameterName Parameter name
     * @param defaultValue  Default value to be returned if parameter is not present
     * @return Parameter value or the default value if not present
     */
    fun optFromQuery(parameterName: String, defaultValue: String): String

    /**
     * Optionally gets from POST parameters if parameter is present
     *
     * @param parameterName Parameter name
     * @param defaultValue  Default value to be returned if parameter is not present
     * @return Parameter value or the default value if not present
     */
    fun optFromPost(parameterName: String, defaultValue: String): String

    /**
     * Returns the value of the specified request header as a String. If the request did not include a header of the
     * specified name, this method returns null. If there are multiple headers with the same name, this method returns
     * the first head in the request. The header name is case insensitive. You can use this method with any request
     * header.
     *
     * @param headerName Header name
     * @return a String containing the value of the requested header, or null if the request does not have a header
     * of that name
     */
    fun getHeader(headerName: String): String?

    /**
     * Returns all values for a given header (if it is present multiple times)
     *
     * @param headerName Header name
     * @return List of header values
     */
    fun getHeaderValues(headerName: String): List<String>

    /**
     * Returns true if the request is multipart, false otherwise
     *
     * @return true if the request is multipart, false otherwise
     */
    fun isMultipart(): Boolean

    /**
     * Returns the HTTP method of the request, e.g. GET, POST, etc. Alias of [.getHttpMethod]
     *
     * @return HTTP method
     */
    fun getMethod(): HttpMethod

    /**
     * Returns the HTTP method of the request, e.g. GET, POST, etc
     *
     * @return HTTP method
     */
    fun getHttpMethod(): HttpMethod

    /**
     * Checks if the request HTTP method matched the specified as parameter
     *
     * @param method Specified method
     * @return true if the HTTP method matches the specified, false otherwise
     */
    fun isMethod(method: HttpMethod): Boolean

    /**
     * Returns server data
     *
     * @return server data
     */
    fun getServerData(): RequestContext.ServerData

    /**
     * Return body of the request
     *
     * @return body of the request
     */
    fun getBody(): String?

    /**
     * Returns raw query string, e.g. param1=3&param2=ff
     *
     * @return Raw query string
     */
    fun getRawQueryString(): String?

    /**
     * Returns all cookies with their string values
     *
     * @return All cookies
     */
    fun getCookies(): List<Cookie>

    /**
     * Returns all headers with their string values
     *
     * @return
     */
    fun getHeaders(): Map<String, String>
    data class ServerData(
        val serverAddress: String,
        val serverName: String,
        val serverProtocol: String,
        val serverPort: Int,
        val requestMethod: String,
        val queryString: String?,
        val httpAccept: String?,
        val httpAcceptCharset: String?,
        val httpAcceptEncoding: String?,
        val httpAcceptLanguage: String?,
        val httpConnection: String?,
        val httpHost: String?,
        val httpReferrer: String?,
        val httpUserAgent: String?,
        val remoteAddress: String?,
        val remoteHost: String?,
        val remotePort: Int,
        val requestUri: String,
        val pathInfo: String?,
    )
}