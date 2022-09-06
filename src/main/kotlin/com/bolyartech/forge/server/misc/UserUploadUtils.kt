package com.bolyartech.forge.server.misc

import com.google.common.io.ByteStreams
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import java.util.*


@Throws(IOException::class)
fun saveUploadedFile(`is`: InputStream, destinationPath: Path) {
    var out: BufferedOutputStream? = null
    try {
        out = BufferedOutputStream(FileOutputStream(destinationPath.toFile()))
        ByteStreams.copy(`is`, out)
    } finally {
        `is`.close()
        out?.close()
    }
}

fun convertStreamToString(`is`: InputStream): String {
    val s = Scanner(`is`).useDelimiter("\\A")
    return if (s.hasNext()) s.next() else ""
}