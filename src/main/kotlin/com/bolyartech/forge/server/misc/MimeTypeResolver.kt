package com.bolyartech.forge.server.misc

/**
 * MIME type resolver interface
 */
interface MimeTypeResolver {
    /**
     * Tries to resolve the MIME type by the file extension
     *
     * @param fileName File name
     * @return MIME type literal
     */
    fun resolveForFilename(fileName: String): String?
}