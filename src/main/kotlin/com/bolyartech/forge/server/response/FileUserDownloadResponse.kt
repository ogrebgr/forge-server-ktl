package com.bolyartech.forge.server.response

import com.bolyartech.forge.server.misc.MimeTypeResolver
import com.google.common.io.ByteStreams
import jakarta.servlet.http.HttpServletResponse
import java.io.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FileUserDownloadResponse constructor(
    private val mimeTypeResolver: MimeTypeResolver,
    private val file: File,
    private val originalFilename: String
) : Response {


    override fun toServletResponse(resp: HttpServletResponse): Long {
        var cl = 0L
        try {
            resp.contentType = mimeTypeResolver.resolveForFilename(file.name)
            val ts = ZonedDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("UTC"))
            val lm = DateTimeFormatter.RFC_1123_DATE_TIME.format(ts)
            resp.setHeader(HttpHeaders.LAST_MODIFIED, lm)
            resp.setHeader("content-disposition", "attachment; filename=\"$originalFilename\"")
            val `is`: InputStream = BufferedInputStream(FileInputStream(file))
            try {
                val out = resp.outputStream
                cl = ByteStreams.copy(`is`, out)
                out.flush()
                out.close()

                return file.length()
            } catch (e: IOException) {
                return cl
            }
        } catch (e: FileNotFoundException) {
            throw ResponseException(e)
        }
    }
}