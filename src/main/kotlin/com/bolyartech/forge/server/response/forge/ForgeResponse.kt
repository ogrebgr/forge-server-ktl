package com.bolyartech.forge.server.response.forge

import com.bolyartech.forge.server.response.HttpHeader
import com.bolyartech.forge.server.response.JsonResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

/**
 * Forge response
 *
 *
 * Forge responses are used by the Forge framework. They provide response result code in a 'X-Forge-Result-Code'
 * header and the result payload/data (if any) in the response body (usually as JSON).
 *
 *
 * Positive result codes indicate successful handling of the request.
 * Negative result codes indicate that there is some error handling the request.
 *
 * @see BasicResponseCodes
 */
open class ForgeResponse(
    private val forgeResultCode: Int,
    str: String = "",
    cookiesToSet: List<Cookie> = emptyList(),
    headersToAdd: List<HttpHeader> = emptyList(),
    enableGzipSupport: Boolean = true
) : JsonResponse(str, cookiesToSet, headersToAdd, enableGzipSupport) {

    constructor(
        forgeResultCode: ForgeResponseCode,
        str: String = "",
        cookiesToSet: List<Cookie> = emptyList(),
        headersToAdd: List<HttpHeader> = emptyList(),
        enableGzipSupport: Boolean = true
    ) : this(
        forgeResultCode.getCode(),
        str, cookiesToSet, headersToAdd, enableGzipSupport
    )


    override fun toServletResponse(resp: HttpServletResponse): Long {
        resp.setHeader(FORGE_RESULT_CODE_HEADER, Integer.toString(forgeResultCode))
        return super.toServletResponse(resp)
    }

    companion object {
        private const val FORGE_RESULT_CODE_HEADER = "X-Forge-Result-Code"
    }
}