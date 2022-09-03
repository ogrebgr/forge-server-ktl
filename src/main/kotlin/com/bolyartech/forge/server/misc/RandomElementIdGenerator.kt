package com.bolyartech.forge.server.misc

import java.util.*
import javax.inject.Inject

interface RandomElementIdGenerator {
    fun generate(): String
    fun generate(totalLength: Int): String
}

class RandomElementIdGeneratorImpl @Inject constructor() : RandomElementIdGenerator {
    private val rng = Random()

    companion object {
        private const val MAX_CHARS_PREFIX = 10
        private const val MIN_BYTES_SUFFIX = 6
        private const val MAX_BYTES_SUFFIX = 8
    }

    private val chars = "abcdefghijklmnopqrstuvwxyz"

    override fun generate(): String {
        val prefixLength = rng.nextInt(MAX_CHARS_PREFIX - 1) + 1

        val prefix = generatePrefix(prefixLength, rng)

        val suffixLength = rng.nextInt(MAX_BYTES_SUFFIX - MIN_BYTES_SUFFIX) + MIN_BYTES_SUFFIX

        val suffix = generateSuffix(suffixLength, rng)

        return prefix + suffix
    }

    override fun generate(totalLength: Int): String {
        if (totalLength < 2) {
            throw IllegalArgumentException("totalLength must be 2 or greater")
        }
        val prefix = generatePrefix(1, rng)
        val suffix = generateSuffix(totalLength - 1, rng)

        return prefix + suffix
    }


    private fun generateSuffix(suffixLength: Int, rng: Random): String {
        val rez = ByteArray(suffixLength)
        rng.nextBytes(rez)

        return rez.toHexString()
    }

    private fun generatePrefix(prefixLength: Int, rng: Random): String {
        var rez = ""

        val l = chars.length

        for (i in 1..prefixLength) {
            rez += chars[rng.nextInt(l)]
        }

        return rez
    }
}