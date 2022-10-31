package com.bolyartech.forge.server.response

import com.bolyartech.forge.server.response.StringResponse.Companion.MIN_SIZE_FOR_GZIP
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
abstract class AbstractStringResponse(
    private val str: String,
    private val cookiesToSet: List<Cookie> = emptyList(),
    private val headersToAdd: List<HttpHeader> = emptyList(),
    private val enableGzipSupport: Boolean = true
) : AbstractResponse(), StringResponse {

    override fun toServletResponse(resp: HttpServletResponse): Long {
        addCookiesAndHeaders(resp)
        resp.status = HttpServletResponse.SC_OK
        resp.contentType = getContentType()
        var cl: Long = 0
        return try {
            val out: OutputStream = if (enableGzipSupport && str.toByteArray().size > MIN_SIZE_FOR_GZIP) {
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