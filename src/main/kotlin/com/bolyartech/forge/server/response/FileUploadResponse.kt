package com.bolyartech.forge.server.response

import com.bolyartech.forge.server.response.StringResponse.Companion.MIN_SIZE_FOR_GZIP
import com.google.common.base.Strings
import com.google.common.io.ByteStreams
import com.google.common.io.CountingOutputStream
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import java.io.*
import java.text.MessageFormat
import java.util.zip.GZIPOutputStream

/**
 * Response for uploading file (from server point of view)
 */
class FileUploadResponse : AbstractResponse {
    private val file: File
    private val enableGzip: Boolean

    /**
     * Creates new FileUploadResponse
     *
     * @param filePath   Path to the file which will be uploaded
     * @param enableGzip if true Gzip compression will be used if supported by the client
     */
    constructor(filePath: String, enableGzip: Boolean) {
        require(!Strings.isNullOrEmpty(filePath)) { "filePath null or empty" }
        file = File(filePath)
        require(file.exists()) { "No such file exist: $filePath" }
        this.enableGzip = enableGzip
    }

    /**
     * @param cookiesToSet list of cookies to be set
     * @param @param       filePath   Path to the file which will be uploaded
     */
    constructor(cookiesToSet: List<Cookie>, filePath: String) : super(cookiesToSet) {
        file = File(filePath)
        require(file.exists()) { "No such file exist: $filePath" }
        enableGzip = false
    }

    /**
     * @param cookiesToSet list of cookies to be set
     * @param filePath     Path to the file which will be uploaded
     * @param enableGzip   if true Gzip compression will be used if supported by the client
     */
    constructor(cookiesToSet: List<Cookie>, filePath: String, enableGzip: Boolean) : super(cookiesToSet) {
        file = File(filePath)
        require(file.exists()) { "No such file exist: $filePath" }
        this.enableGzip = enableGzip
    }

    override fun toServletResponse(resp: HttpServletResponse): Long {
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