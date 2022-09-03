package com.bolyartech.forge.server.route

class InvalidParameterValueException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}