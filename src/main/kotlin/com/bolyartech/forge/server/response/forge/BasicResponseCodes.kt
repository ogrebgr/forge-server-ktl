package com.bolyartech.forge.server.response.forge

/**
 * Class containing basic response codes for Forge endpoints
 */
class BasicResponseCodes {
    /**
     * Codes for OK
     */
    enum class Oks(code: Int) : ForgeResponseCode {
        OK(1);

        // used as general code that indicates success
        override val code = 0
    }

    /**
     * Codes for errors
     */
    enum class Errors(code: Int) : ForgeResponseCode {
        ERROR(-1),  // used as general error when we cant/don't want to specify more details
        MISSING_PARAMETERS(-2),  // missing required parameters
        REQUIRES_HTTPS(-3),  // HTTPS protocol must be used
        INVALID_PARAMETER_VALUE(-4),  // parameter value is invalid. For example: string is passed where int is expected
        INTERNAL_SERVER_ERROR(-5),  // some serious problem occurred on the server
        UPGRADE_NEEDED(-6);

        override val code = 0


        companion object {
            private val MAP: Map<Int, Errors> = Errors.values().associateBy { it.code }

            @Throws(java.lang.IllegalArgumentException::class)
            fun fromInt(i: Int): Errors {
                if (MAP[i] != null) {
                    return MAP[i]!!
                } else {
                    throw java.lang.IllegalArgumentException("Invalid Errors id: $i")
                }
            }
        }
    }
}