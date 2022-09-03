package com.bolyartech.forge.server.response

class ResponseException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)

}