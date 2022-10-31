package com.bolyartech.forge.server.response

import com.bolyartech.forge.server.response.StringResponse.Companion.MIN_SIZE_FOR_GZIP
import com.google.common.io.ByteStreams
import com.google.common.io.CountingOutputStream
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import java.io.*
import java.text.MessageFormat
import java.util.zip.GZIPOutputStream

/**
 * Response for uploading file (from server point of view, from user POV it is download)
 */
class FileUploadResponse(
    filePath: String,
    private val cookiesToSet: List<Cookie> = emptyList(),
    headersToAdd: List<HttpHeader> = emptyList(),
    private val enableGzip: Boolean = true
) : AbstractResponse(cookiesToSet, headersToAdd) {
    private val file: File = File(filePath)


    override fun toServletResponse(resp: HttpServletResponse): Long {
        if (!file.exists()) {
            throw IllegalArgumentException("No such file ${file.absolutePath}")
        }

        addCookiesAndHeaders(resp)
        resp.contentType = HttpHeaders.CONTENT_TYPE_OCTET
        resp.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            MessageFormat.format(HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT, file.name)
        )
        var cl: Long = 0
        val `is`: InputStream
        return try {
            `is` = BufferedInputStream(FileInputStream(file))
            val out: OutputStream
            out = if (enableGzip && file.length() > MIN_SIZE_FOR_GZIP) {
                resp.setHeader(HttpHeaders.CONTENT_ENCODING, HttpHeaders.CONTENT_ENCODING_GZIP)
                CountingOutputStream(GZIPOutputStream(resp.outputStream, true))
            } else {
                resp.setContentLength(file.length().toInt())
                resp.outputStream
            }
            cl = ByteStreams.copy(`is`, out)
            if (enableGzip && cl > MIN_SIZE_FOR_GZIP) {
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
}