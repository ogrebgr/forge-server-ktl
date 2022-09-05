package com.bolyartech.forge.server.response

import com.bolyartech.forge.server.misc.MimeTypeResolver
import com.google.common.io.ByteStreams
import com.google.common.io.CountingOutputStream
import jakarta.servlet.http.HttpServletResponse
import java.io.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPOutputStream

/**
 * Response which uses static file as a content
 * Use this class for static HTML files, CSS or images (PNG, JPG, etc.)
 */
class StaticFileResponse : AbstractResponse {
    private val file: File
    private val enableGzip: Boolean
    private val mimeType: String?

    /**
     * Creates new StaticFileResponse
     *
     * @param mimeTypeResolver MIME type resolver (resolves the MIME type by the file extension)
     * @param file             File to be used as content
     * @param enableGzip       if true Gzip compression will be used if the client supports it
     */
    constructor(mimeTypeResolver: MimeTypeResolver, file: File, enableGzip: Boolean) {
        this.file = file
        this.enableGzip = enableGzip
        mimeType = mimeTypeResolver.resolveForFilename(this.file.name)
    }

    /**
     * Creates new StaticFileResponse
     *
     * @param file       File to be used as content
     * @param enableGzip if true Gzip compression will be used if the client supports it
     * @param mimeType   MIME type to be used
     */
    constructor(file: File, enableGzip: Boolean, mimeType: String?) {
        this.file = file
        this.enableGzip = enableGzip
        this.mimeType = mimeType
    }

    override fun toServletResponse(resp: HttpServletResponse): Long {
        var cl: Long = 0
        addCookiesAndHeaders(resp)
        return try {
            resp.contentType = mimeType
            val ts = ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("UTC"))
            val lm = DateTimeFormatter.RFC_1123_DATE_TIME.format(ts)
            resp.setHeader(HttpHeaders.LAST_MODIFIED, lm)
            val `is`: InputStream = BufferedInputStream(FileInputStream(file))
            try {
                val out: OutputStream
                if (enableGzip && file.length() > StringResponse.MIN_SIZE_FOR_GZIP) {
                    resp.setHeader(HttpHeaders.CONTENT_ENCODING, HttpHeaders.CONTENT_ENCODING_GZIP)
                    out = CountingOutputStream(GZIPOutputStream(resp.outputStream, true))
                } else {
                    out = resp.outputStream
                }
                cl = ByteStreams.copy(`is`, out)
                if (enableGzip && cl > StringResponse.MIN_SIZE_FOR_GZIP) {
                    cl = (out as CountingOutputStream).count
                }
                out.flush()
                out.close()
                cl
            } catch (e: IOException) {
                // ignore
                cl
            }
        } catch (e: FileNotFoundException) {
            // ignore
            cl
        }
    }
}