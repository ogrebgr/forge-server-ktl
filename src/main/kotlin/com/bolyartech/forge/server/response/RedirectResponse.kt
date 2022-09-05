package com.bolyartech.forge.server.response

import jakarta.servlet.http.HttpServletResponse

/**
 * Redirect response which instructs the browsers to redirect
 */
interface RedirectResponse : Response {
    /**
     * Returns target location
     *
     * @return target location to where to redirect
     */
    fun getLocation(): String
}

sealed class RedirectResponseImpl(private val location: String, private val code: Int) : RedirectResponse {
    override fun getLocation(): String {
        return location
    }

    override fun toServletResponse(resp: HttpServletResponse): Long {
        resp.status = code
        resp.setHeader(HEADER_LOCATION, location)
        return 0
    }

    companion object {
        private const val HEADER_LOCATION = "Location"
    }
}

class RedirectResponse300MultipleChoices(location: String) : RedirectResponseImpl(location, HttpServletResponse.SC_MULTIPLE_CHOICES)
class RedirectResponse301MovedPermanently(location: String) : RedirectResponseImpl(location, HttpServletResponse.SC_MOVED_PERMANENTLY)
class RedirectResponse302MovedTemporarily(location: String) : RedirectResponseImpl(location, HttpServletResponse.SC_MOVED_TEMPORARILY)
class RedirectResponse303SeeOther(location: String) : RedirectResponseImpl(location, HttpServletResponse.SC_SEE_OTHER)
class RedirectResponse304NotModified(location: String) : RedirectResponseImpl(location, HttpServletResponse.SC_NOT_MODIFIED)
class RedirectResponse305UseProxy(location: String) : RedirectResponseImpl(location, HttpServletResponse.SC_USE_PROXY)
class RedirectResponse307TemporaryRedirect(location: String) :
    RedirectResponseImpl(location, HttpServletResponse.SC_TEMPORARY_REDIRECT)