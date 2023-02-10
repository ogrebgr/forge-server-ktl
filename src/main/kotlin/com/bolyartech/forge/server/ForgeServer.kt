package com.bolyartech.forge.server

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import com.bolyartech.forge.server.config.ForgeConfigurationException
import com.bolyartech.forge.server.config.ForgeServerConfiguration
import com.bolyartech.forge.server.config.ForgeServerConfigurationLoaderFile
import com.bolyartech.forge.server.config.detectConfigurationDirectory
import com.bolyartech.forge.server.db.DbConfiguration
import com.bolyartech.forge.server.db.DbConfigurationLoaderFile
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.pathString

interface ForgeServer {
    @Throws(ForgeConfigurationException::class)
    fun start(configurationPack: ConfigurationPack, fileSystem: FileSystem)
    fun shutdown()

    fun onStart()
    fun onBeforeWebServerStart()
    fun onAfterWebServerStart(webServerStopper: WebServerStopper)
    fun onShutdown()

    fun createDbDataSource(dbConfig: DbConfiguration): ComboPooledDataSource
    fun createWebServer(
        forgeConfig: ConfigurationPack,
        dbDataSource: ComboPooledDataSource,
        fileSystem: FileSystem
    ): WebServer

    data class ConfigurationPack(
        val configurationDirectory: Path,
        val forgeServerConfiguration: ForgeServerConfiguration,
        val dbConfiguration: DbConfiguration,
    )

    companion object {
        fun createDataSourceHelper(dbConf: DbConfiguration): ComboPooledDataSource {
            val p = Properties(System.getProperties())
            p["com.mchange.v2.log.MLog"] = "com.mchange.v2.log.FallbackMLog"
            p["com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL"] = "OFF"
            System.setProperties(p)

            val comboPooledDataSource = ComboPooledDataSource()
            comboPooledDataSource.jdbcUrl = dbConf.dbDsn
            comboPooledDataSource.user = dbConf.dbUsername
            comboPooledDataSource.password = dbConf.dbPassword
            comboPooledDataSource.maxStatements = dbConf.cacheMaxStatements
            comboPooledDataSource.initialPoolSize = dbConf.initialPoolSize
            comboPooledDataSource.minPoolSize = dbConf.minPoolSize
            comboPooledDataSource.maxPoolSize = dbConf.maxPoolSize
            comboPooledDataSource.idleConnectionTestPeriod = dbConf.idleConnectionTestPeriod
            comboPooledDataSource.isTestConnectionOnCheckin = dbConf.testConnectionOnCheckIn
            comboPooledDataSource.isTestConnectionOnCheckout = dbConf.testConnectionOnCheckout
            comboPooledDataSource.connectionCustomizerClassName = "com.bolyartech.forge.server.db.C3p0ConnectionCustomizer"

            return comboPooledDataSource
        }

        fun loadConfigurationPack(fs: FileSystem, args: Array<String>): ConfigurationPack {
            val filesystem = FileSystems.getDefault()
            val configDir = detectConfigurationDirectory(filesystem, args)
            if (configDir == null) {
                throw ForgeConfigurationException("Cannot detect the configuration directory. Exiting.")
            }

            val forgeConf = ForgeServerConfigurationLoaderFile(configDir).load()
            val dbConf = DbConfigurationLoaderFile(configDir).load()

            return ConfigurationPack(configDir, forgeConf, dbConf)
        }

        fun initLog(logger: Logger, configDir: String, logFilenamePrefix: String = "", serverNameSuffix: String = "") {
            val context = LoggerFactory.getILoggerFactory() as LoggerContext
            val jc = JoranConfigurator()
            jc.context = context
            context.reset()

            context.putProperty("application-name", logFilenamePrefix + serverNameSuffix)

            val f = File(configDir, "logback.xml")
            println("Will try logback config: " + f.absolutePath)
            if (f.exists()) {
                val logbackConfigFilePath = f.absolutePath
                try {
                    jc.doConfigure(logbackConfigFilePath)
                    logger.info("+++ logback initialized OK")
                } catch (e: JoranException) {
                    e.printStackTrace()
                }
            } else {
                println("!!! No logback configuration file found. Using default configuration.")
            }
        }
    }
}


abstract class AbstractForgeServer() : ForgeServer {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var isStarted = false
    private var isShutdown = false

    private var config: ForgeServer.ConfigurationPack? = null
    private var fileSystem: FileSystem? = null

    private var webServer: WebServer? = null

    @Override
    override fun start(configurationPack: ForgeServer.ConfigurationPack, fileSystem: FileSystem) {
        require(!isStarted)
        require(!isShutdown)

        Runtime.getRuntime().addShutdownHook(Thread(ShutDownRunnable()))
        isStarted = true

        onStart()

        config = configurationPack
        this.fileSystem = fileSystem

        if (config!!.forgeServerConfiguration.uploadsDirectory.isNotEmpty()) {
            val ulDir = this.fileSystem!!.getPath(config!!.forgeServerConfiguration.uploadsDirectory)
            if (!ulDir.exists()) {
                logger.error("Uploads dir specified in forge.conf (${ulDir.pathString}) does not exists. Leave blank if not used.")
                return
            }
        }

        if (config!!.forgeServerConfiguration.downloadsDirectory.isNotEmpty()) {
            val dlDir = this.fileSystem!!.getPath(config!!.forgeServerConfiguration.downloadsDirectory)
            if (!dlDir.exists()) {
                logger.error("Downloads dir specified in forge.conf (${dlDir.pathString}) does not exists. Leave blank if not used.")
                return
            }
        }

        val dbDataSource = createDbDataSource(config!!.dbConfiguration)
        dbDataSource.connection.use {
            // just testing if acquiring of a db connection is successful
            logger.info("Using DB ${config!!.dbConfiguration.dbDsn}")
        }


        onBeforeWebServerStart()
        webServer = createWebServer(config!!, dbDataSource, fileSystem)
        webServer!!.start()
        onAfterWebServerStart(webServer!!)
    }

    @Override
    override fun shutdown() {
        require(isStarted)

        ShutDownRunnable().run()

        isStarted = false
        isShutdown = true
    }

    inner class ShutDownRunnable : Runnable {
        override fun run() {
            synchronized(this@AbstractForgeServer) {
                webServer?.stop()
                onShutdown()
            }
        }
    }
}


abstract class AbstractForgeServerAdapter : AbstractForgeServer() {
    override fun onStart() {
    }

    override fun onBeforeWebServerStart() {
    }

    override fun onAfterWebServerStart(webServerStopper: WebServerStopper) {
    }

    override fun onShutdown() {
    }
}