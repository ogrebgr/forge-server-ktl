package com.bolyartech.forge.server.response.builders

import com.bolyartech.forge.server.response.RedirectResponse
import jakarta.servlet.http.HttpServletResponse

class RedirectResponseBuilder(private val redirectStatus: RedirectStatus, private val locationField: String) :
    AbstractResponseBuilder() {

    enum class RedirectStatus(val status: Int) {
        MULTIPLE_CHOICES_300(300),
        MOVED_PERMANENTLY_301(301),
        MOVED_TEMPORARILY_302(302),
        SEE_OTHER_303(303),
        NOT_MODIFIED_304(304),
        USE_PROXY_305(305),
        TEMPORARY_REDIRECT_307(307);
    }

    override fun build(): RedirectResponse {
        val ret = object : RedirectResponse {

            override fun toServletResponse(resp: HttpServletResponse): Long {
                getCookies().forEach {
                    resp.addCookie(it)
                }
                getHeaders().forEach {
                    resp.addHeader(it.header, it.value)
                }

                resp.status = redirectStatus.status
                resp.setHeader("Location", getLocation())

                return 0L
            }

            override fun getLocation(): String {
                return locationField
            }
        }

        return ret
    }
}

