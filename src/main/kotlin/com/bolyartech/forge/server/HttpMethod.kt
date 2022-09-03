package com.bolyartech.forge.server

enum class HttpMethod(val methodName: String) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    UNSUPPORTED("UNSUPPORTED");

}