package com.bolyartech.forge.server.response

import com.google.common.io.ByteStreams
import com.google.common.io.CountingOutputStream
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPOutputStream

/**
 * Base class for specialized str responses
 */
abstract class AbstractStringResponse : AbstractResponse, StringResponse {
    private lateinit var str: String
    private var enableGzipSupport: Boolean = true

    companion object {
        private const val MIN_SIZE_FOR_GZIP = 500
    }

    /**
     * Creates new AbstractStringResponse
     *
     * @param str       String of the response
     */
    constructor(str: String) : super() {
        this.str = str

    }

    /**
     * Creates new AbstractStringResponse
     *
     * @param cookiesToSet list of cookies to be set
     * @param str       String of the response
     */
    constructor(cookiesToSet: List<Cookie>, str: String) : super(cookiesToSet) {
        enableGzipSupport = false
    }

    /**
     * Creates new AbstractStringResponse
     *
     * @param cookiesToSet      list of cookies to be set. Pass empty list if no cookies have to be added. Don't pass null because it will throw NullPointerException
     * @param headersToAdd      list of headers to be added. If the header already exists, it will be overwritten
     * @param str            String of the response
     * @param enableGzipSupport if true Gzip compression will be used if the client supports it
     */
    constructor(
        cookiesToSet: List<Cookie>, headersToAdd: List<HttpHeader>, str: String,
        enableGzipSupport: Boolean
    ) : super(cookiesToSet, headersToAdd) {
        this.str = str
        this.enableGzipSupport = enableGzipSupport
    }

    /**
     * Creates new AbstractStringResponse
     *
     * @param cookiesToSet      list of cookies to be set
     * @param str            String of the response
     * @param enableGzipSupport if true Gzip compression will be used if the client supports it
     */
    constructor(cookiesToSet: List<Cookie>, str: String, enableGzipSupport: Boolean) : super(cookiesToSet) {
        this.str = str
        this.enableGzipSupport = enableGzipSupport
    }

    /**
     * Creates new AbstractStringResponse
     *
     * @param str            String of the response
     * @param enableGzipSupport if true Gzip compression will be used if the client supports it
     */
    constructor(str: String, enableGzipSupport: Boolean) {
        this.str = str
        this.enableGzipSupport = enableGzipSupport
    }


    override fun toServletResponse(resp: HttpServletResponse): Long {
        addCookiesAndHeaders(resp)
        resp.status = HttpServletResponse.SC_OK
        resp.contentType = getContentType()
        var cl: Long = 0
        return try {
            val out: OutputStream
            out = if (enableGzipSupport && str.toByteArray().size > MIN_SIZE_FOR_GZIP) {
                resp.setHeader(HttpHeaders.CONTENT_ENCODING, HttpHeaders.CONTENT_ENCODING_GZIP)
                CountingOutputStream(GZIPOutputStream(resp.outputStream, true))
            } else {
                resp.setContentLength(str.toByteArray().size)
                resp.outputStream
            }
            val `is`: InputStream = ByteArrayInputStream(str.toByteArray(charset("UTF-8")))
            cl = ByteStreams.copy(`is`, out)
            if (enableGzipSupport && cl > MIN_SIZE_FOR_GZIP) {
                cl = (out as CountingOutputStream).count
            }
            out.flush()
            out.close()
            cl
        } catch (e: IOException) {
            // ignore
            cl
        }
    }


    override fun getString(): String {
        return str
    }

    protected abstract fun getContentType(): String
}