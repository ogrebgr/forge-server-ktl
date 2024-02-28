package com.bolyartech.forge.server.misc

class MimeTypeResolverImpl : MimeTypeResolver {
    private val FALLBACK_MIME = "application/octet-stream"

    private val extToMime: Map<String, String> = object : HashMap<String, String>() {
        init {
            put("au", "audio/basic")
            put("avi", "video/msvideo,video/avi,video/x-msvideo")
            put("bmp", "image/bmp")
            put("bz2", "application/x-bzip2")
            put("css", "text/css")
            put("dtd", "application/xml-dtd")
            put("doc", "application/msword")
            put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
            put("eot", "application/vnd.ms-fontobject")
            put("es", "application/ecmascript")
            put("exe", "application/octet-stream")
            put("gif", "image/gif")
            put("gz", "application/x-gzip")
            put("hqx", "application/mac-binhex40")
            put("html", "text/html")
            put("htm", "text/html")
            put("jar", "application/java-archive")
            put("jpg", "image/jpeg")
            put("js", "application/javascript")
            put("midi", "audio/x-midi")
            put("mp3", "audio/mpeg")
            put("mp4", "video/mp4")
            put("mpeg", "video/mpeg")
            put("ogg", "audio/vorbis,application/ogg")
            put("otf", "application/font-otf")
            put("pdf", "application/pdf")
            put("pl", "application/x-perl")
            put("png", "image/png")
            put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template")
            put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow")
            put("ppt", "application/vnd.ms-powerpointtd")
            put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
            put("ps", "application/postscript")
            put("qt", "video/quicktime")
            put("ra", "audio/x-pn-realaudio,audio/vnd.rn-realaudio")
            put("rar", "application/x-rar-compressed")
            put("ram", "audio/x-pn-realaudio,audio/vnd.rn-realaudio")
            put("rdf", "application/rdf,application/rdf+xml")
            put("rtf", "application/rtf")
            put("sgml", "text/sgml")
            put("sit", "application/x-stuffit")
            put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide")
            put("svg", "image/svg+xml")
            put("swf", "application/x-shockwave-flash")
            put("tgz", "application/x-tar")
            put("tiff", "image/tiff")
            put("tsv", "text/tab-separated-values")
            put("ttf", "application/font-ttf")
            put("txt", "text/plain")
            put("wav", "audio/wav,audio/x-wav")
            put("webm", "video/webm")
            put("woff", "application/font-woff")
            put("woff2", "application/font-woff2")
            put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12")
            put("xls", "application/vnd.ms-excel")
            put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12")
            put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template")
            put("xml", "application/xml")
            put("zip", "application/zip,application/x-compressed-zip")
        }
    }


    override fun resolveForFilename(fileName: String): String? {
        val fileExtension = fileName.replace("^.*\\.(.*)$".toRegex(), "$1").trim { it <= ' ' }
        var ret: String? = FALLBACK_MIME
        if (fileExtension.isNotEmpty()) {
            val mime = extToMime[fileExtension]
            if (mime != null) {
                ret = mime
            }
        }
        return ret
    }
}