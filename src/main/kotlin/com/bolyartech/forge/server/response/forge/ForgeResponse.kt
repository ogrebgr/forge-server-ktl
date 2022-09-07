package com.bolyartech.forge.server.response.forge

import com.bolyartech.forge.server.response.JsonResponse
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
open class ForgeResponse : JsonResponse {
    /**
     * Returns result code
     *
     * @return Result code
     */
    val resultCode: Int

    /**
     * Creates new ForgeResponse
     *
     * @param resultCode Result code
     */
    constructor(resultCode: Int) : super("") {
        this.resultCode = resultCode
    }

    /**
     * Creates new ForgeResponse
     *
     * @param resultCode Result code
     */
    constructor(resultCode: ForgeResponseCode) : super("") {
        this.resultCode = resultCode.code
    }

    /**
     * Creates new ForgeResponse
     *
     * @param resultCode Result code
     * @param string     Data
     */
    constructor(resultCode: Int, string: String?) : super(string!!) {
        this.resultCode = resultCode
    }

    /**
     * Creates new ForgeResponse
     *
     * @param resultCode        Result code
     * @param string            Data
     * @param enableGzipSupport if true Gzip compression will be used if the client supports it
     */
    constructor(resultCode: Int, string: String?, enableGzipSupport: Boolean) : super(string!!, enableGzipSupport) {
        this.resultCode = resultCode
    }

    /**
     * Creates new ForgeResponse
     *
     * @param resultCode Result code
     * @param string     Data
     */
    constructor(resultCode: ForgeResponseCode, string: String?) : super(string!!) {
        this.resultCode = resultCode.code
    }

    /**
     * Creates new ForgeResponse
     *
     * @param resultCode        Result code
     * @param string            Data
     * @param enableGzipSupport if true Gzip compression will be used if the client supports it
     */
    constructor(resultCode: ForgeResponseCode, string: String?, enableGzipSupport: Boolean) : super(
        string!!,
        enableGzipSupport
    ) {
        this.resultCode = resultCode.code
    }

    override fun toServletResponse(resp: HttpServletResponse): Long {
        resp.setHeader(FORGE_RESULT_CODE_HEADER, Integer.toString(resultCode))
        return super.toServletResponse(resp)
    }

    /**
     * Returns the payload
     *
     * @return Payload
     */
    val payload: String
        get() = getString()

    companion object {
        private const val FORGE_RESULT_CODE_HEADER = "X-Forge-Result-Code"
    }
}