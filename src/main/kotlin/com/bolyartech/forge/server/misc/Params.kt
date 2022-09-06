package com.bolyartech.forge.server.misc

import com.bolyartech.forge.server.route.InvalidParameterValueException
import com.bolyartech.forge.server.route.MissingParameterValueException
import com.bolyartech.forge.server.route.RequestContext
import com.google.common.base.Strings
import java.util.*

/**
 * Utility class for GET/POST/PI parameters
 */
@Suppress("unused")
class Params private constructor() {
    companion object {
        private const val GET = "GET"
        private const val POST = "POST"

        @Throws(MissingParameterValueException::class)
        fun allPresentOrDie(vararg pars: String) {
            require(pars.isNotEmpty()) { "pars is empty" }
            for (par in pars) {
                if (Strings.isNullOrEmpty(par)) {
                    throw MissingParameterValueException(par)
                }
            }
        }

        /**
         * Checks if all strings are non-null and non-empty
         *
         * @param pars Strings to be checked
         * @return true if all strings are non-null and non-empty
         */
        fun areAllPresent(vararg pars: String): Boolean {
            var ret = true
            require(pars.isNotEmpty()) { "pars is empty" }
            for (par in pars) {
                if (Strings.isNullOrEmpty(par)) {
                    ret = false
                    break
                }
            }
            return ret
        }

        /**
         * Extracts long parameter's value from POST parameters
         *
         * @param ctx           Context from which the value will be extracted
         * @param parameterName Parameter name
         * @return extracted value
         * @throws MissingParameterValueException if there is no value for this parameter
         * @throws InvalidParameterValueException if the value is present but cannot be parsed as long
         */
        @Throws(MissingParameterValueException::class, InvalidParameterValueException::class)
        fun extractLongFromPost(ctx: RequestContext, parameterName: String): Long {
            return extractLongHelper(parameterName, ctx.getFromPost(parameterName))
        }

        /**
         * Extracts long parameter's value from query parameters
         *
         * @param ctx           Context from which the value will be extracted
         * @param parameterName Parameter name
         * @return extracted value
         * @throws MissingParameterValueException if there is no value for this parameter
         * @throws InvalidParameterValueException if the value is present but cannot be parsed as long
         */
        @Throws(InvalidParameterValueException::class, MissingParameterValueException::class)
        fun extractLongFromQuery(ctx: RequestContext, parameterName: String): Long {
            return extractLongHelper(parameterName, ctx.getFromQuery(parameterName))
        }

        @Throws(MissingParameterValueException::class, InvalidParameterValueException::class)
        private fun extractLongHelper(
            parameterName: String,
            value: String?
        ): Long {
            if (value == null) {
                throw MissingParameterValueException(parameterName)
            }
            return try {
                value.toLong()
            } catch (e: NumberFormatException) {
                throw InvalidParameterValueException(parameterName)
            }
        }

        /**
         * Extracts integer parameter's value from POST parameters
         *
         * @param ctx           Context from which the value will be extracted
         * @param parameterName Parameter name
         * @return extracted value
         * @throws MissingParameterValueException if there is no value for this parameter
         * @throws InvalidParameterValueException if the value is present but cannot be parsed as int
         */
        @Throws(MissingParameterValueException::class, InvalidParameterValueException::class)
        fun extractIntFromPost(ctx: RequestContext, parameterName: String): Int {
            return extractIntHelper(parameterName, ctx.getFromPost(parameterName))
        }

        /**
         * Extracts integer parameter's value from GET parameters
         *
         * @param ctx           Context from which the value will be extracted
         * @param parameterName Parameter name
         * @return extracted value
         * @throws MissingParameterValueException if there is no value for this parameter
         * @throws InvalidParameterValueException if the value is present but cannot be parsed as int
         */
        @Throws(InvalidParameterValueException::class, MissingParameterValueException::class)
        fun extractIntFromQuery(ctx: RequestContext, parameterName: String): Int {
            return extractIntHelper(parameterName, ctx.getFromQuery(parameterName))
        }

        @Throws(MissingParameterValueException::class, InvalidParameterValueException::class)
        private fun extractIntHelper(
            parameterName: String,
            value: String?
        ): Int {
            if (value == null) {
                throw MissingParameterValueException(parameterName)
            }
            return try {
                value.toInt()
            } catch (e: NumberFormatException) {
                throw InvalidParameterValueException(parameterName)
            }
        }

        @Throws(InvalidParameterValueException::class)
        fun optIntFromPost(ctx: RequestContext, parameterName: String): Optional<Int> {
            return optIntHelper(parameterName, ctx.getFromPost(parameterName))
        }

        @Throws(InvalidParameterValueException::class)
        fun optIntFromGet(ctx: RequestContext, parameterName: String): Optional<Int> {
            return optIntHelper(parameterName, ctx.getFromQuery(parameterName))
        }

        @Throws(InvalidParameterValueException::class)
        private fun optIntHelper(parameterName: String, value: String?): Optional<Int> {
            return if (value == null) {
                Optional.empty()
            } else try {
                Optional.of(value.toInt())
            } catch (e: NumberFormatException) {
                throw InvalidParameterValueException(parameterName)
            }
        }
    }
}