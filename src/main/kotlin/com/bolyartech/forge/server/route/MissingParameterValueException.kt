package com.bolyartech.forge.server.route

class MissingParameterValueException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}