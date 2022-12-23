package com.bolyartech.forge.server.misc


fun ByteArray.toHexString(): String {
    return this.joinToString("") {
        java.lang.String.format("%02x", it)
    }
}

private const val HEX_CHARS = "0123456789abcdef"

/**
 * Converts hex string to ByteArray
 * @throws IllegalArgumentException if the string contains non hex characters
 */
fun String.hexStringToByteArray(): ByteArray {

    val result = ByteArray(length / 2)
    val tmp = this.toLowerCase()
    for (i in 0 until length step 2) {
        val firstIndex = HEX_CHARS.indexOf(tmp[i])
        val secondIndex = HEX_CHARS.indexOf(tmp[i + 1])

        if (firstIndex == -1 || secondIndex == -1) {
            throw IllegalArgumentException("Not a hex string")
        }

        val octet = firstIndex.shl(4).or(secondIndex)
        result.set(i.shr(1), octet.toByte())
    }

    return result
}
