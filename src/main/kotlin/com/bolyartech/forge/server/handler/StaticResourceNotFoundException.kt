package com.bolyartech.forge.server.handler

class StaticResourceNotFoundException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
}