package com.bolyartech.forge.server


interface WebServerStopper {
    fun stop()
}

interface WebServer : WebServerStopper {
    fun start()
    fun getInstrumentation(): WebServerInstrumentationReader
}

interface WebServerInstrumentationReader {
    fun getQueueSize(): Int
    fun getReadyThreads(): Int
    fun getUtilizationRate(): Double
}
