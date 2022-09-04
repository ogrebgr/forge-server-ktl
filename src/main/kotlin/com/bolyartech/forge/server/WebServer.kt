package com.bolyartech.forge.server


interface WebServerStopper {
    fun stop()
}

interface WebServer : WebServerStopper {
    fun start()
}