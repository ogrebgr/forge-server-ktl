package com.bolyartech.forge.server.handler

import com.bolyartech.forge.server.misc.MimeTypeResolver
import com.bolyartech.forge.server.response.HttpHeaders
import com.bolyartech.forge.server.response.Response
import com.bolyartech.forge.server.response.StaticFileResponse
import com.bolyartech.forge.server.route.RequestContext
import java.io.File

/**
 * Handler for static files like HTML and CSS files
 */
class StaticFileHandler constructor(
    sourceDir: String,
    private val mimeTypeResolver: MimeTypeResolver,
    private val enableGzip: Boolean = true,
    directoryIndexFilenames: String = ""
) : RouteHandlerRuntimeResolved {
    private val directoryIndexes: MutableList<String> = mutableListOf()
    private val sourceDirFinal = if (sourceDir.endsWith(File.separator)) {
        sourceDir
    } else {
        sourceDir + File.separator
    }

    init {
        if (directoryIndexFilenames.isNotEmpty()) {
            val filenames: Array<String> = directoryIndexFilenames.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (f in filenames) {
                require(!f.contains(File.separator)) { "directoryIndexFilenames items cannot contain path separator: $f" }
                directoryIndexes.add(f)
            }
        }
    }

    @Throws(StaticResourceNotFoundException::class)
    override fun handle(ctx: RequestContext): Response {
        val file = File(sourceDirFinal + ctx.getPathInfoString())
        return if (file.exists() && file.isFile) {
            val actualEnableGzip = enableGzip && isSupportingGzip(ctx)
            StaticFileResponse(mimeTypeResolver, file, actualEnableGzip)
        } else {
            if (file.isDirectory && directoryIndexes.size > 0) {
                for (f in directoryIndexes) {
                    val tmp = File(file, f)
                    if (tmp.exists() && tmp.isFile) {
                        val actualEnableGzip = enableGzip && isSupportingGzip(ctx)
                        return StaticFileResponse(mimeTypeResolver, tmp, actualEnableGzip)
                    }
                }
            }
            throw StaticResourceNotFoundException("Cannot find file " + ctx.getPathInfoString())
        }
    }

    companion object {
        fun isSupportingGzip(ctx: RequestContext): Boolean {
            val values: List<String> = ctx.getHeaderValues(HttpHeaders.ACCEPT_ENCODING)
            return if (values != null) {
                for (`val` in values) {
                    if (`val`.contains(",")) {
                        val exploded = `val`.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        for (s in exploded) {
                            if (s.contains(HttpHeaders.CONTENT_ENCODING_GZIP)) {
                                return true
                            }
                        }
                    } else {
                        if (`val`.contains(HttpHeaders.CONTENT_ENCODING_GZIP)) {
                            return true
                        }
                    }
                }
                false
            } else {
                false
            }
        }
    }

    override fun willingToHandle(urlPath: String): Boolean {
        val file = File(sourceDirFinal + urlPath)
        return if (urlPath.endsWith("/")) {
            if (file.isDirectory && directoryIndexes.size > 0) {
                for (f in directoryIndexes) {
                    val tmp = File(file, f)
                    if (tmp.exists() && tmp.isFile) {
                        return true
                    }
                }
            }

            false
        } else {
            file.exists() && file.isFile
        }
    }
}