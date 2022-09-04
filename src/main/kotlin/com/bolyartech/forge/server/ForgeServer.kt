package com.bolyartech.forge.server

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import com.bolyartech.forge.server.config.ForgeConfigurationException
import com.bolyartech.forge.server.config.ForgeServerConfiguration
import com.bolyartech.forge.server.config.ForgeServerConfigurationLoaderFile
import com.bolyartech.forge.server.config.detectConfigurationDirectory
import com.bolyartech.forge.server.db.C3p0DbPool
import com.bolyartech.forge.server.db.DbConfiguration
import com.bolyartech.forge.server.db.DbConfigurationLoaderFile
import com.bolyartech.forge.server.db.DbPool
import com.mchange.v2.c3p0.ComboPooledDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.*
import kotlin.io.path.pathString

interface ForgeServer {
    @Throws(ForgeConfigurationException::class)
    fun start(fs: FileSystem, cliArgs: Array<String>)
    fun shutdown()

    fun onStart()
    fun onBeforeWebServerStart()
    fun onAfterWebServerStart(webServerStopper: WebServerStopper)
    fun onShutdown()

    fun createWebServer(
        fs: FileSystem,
        forgeConfig: AbstractForgeServer.ConfigurationPack,
        dbDataSource: ComboPooledDataSource
    ): WebServer
}


abstract class AbstractForgeServer() : ForgeServer {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var isStarted = false
    private var isShutdown = false

    private var currentConfig: ConfigurationPack? = null
    private var currentFs: FileSystem? = null
    private var currentCliArgs: Array<String>? = null
    private var currentDbPool: DbPool? = null

    private var webServer: WebServer? = null

    @Override
    override fun start(fs: FileSystem, cliArgs: Array<String>) {
        require(!isStarted)
        require(!isShutdown)

        onStart()

        currentConfig = loadConfigurationPack(fs, cliArgs)
        logger.info("+++ forge.conf loaded successfully")
        initLog(logger, currentConfig!!.configurationDirectory.pathString, currentConfig!!.forgeServerConfiguration.serverLogName)

        val dbDataSource = createDataSource(currentConfig!!.dbConfiguration)
        currentDbPool = C3p0DbPool(dbDataSource)
        currentDbPool!!.connection.use {
            // just testing if acquiring of a db connection is successfull
        }

        currentFs = fs
        currentCliArgs = cliArgs

        onBeforeWebServerStart()
        webServer = createWebServer(fs, currentConfig!!, dbDataSource)
        webServer!!.start()
        onAfterWebServerStart(webServer!!)


        isStarted = true
    }

    @Override
    override fun shutdown() {
        require(!isStarted)

        isStarted = false
        isShutdown = true
    }

    data class ConfigurationPack(
        val configurationDirectory: Path,
        val forgeServerConfiguration: ForgeServerConfiguration,
        val dbConfiguration: DbConfiguration,
    )

    private fun loadConfigurationPack(fs: FileSystem, args: Array<String>): ConfigurationPack {
        val filesystem = FileSystems.getDefault()
        val configDir = detectConfigurationDirectory(filesystem, args)
        if (configDir == null) {
            throw ForgeConfigurationException("Cannot detect the configuration directory. Exiting.")
        }

        val forgeConf = ForgeServerConfigurationLoaderFile(configDir).load()
        val dbConf = DbConfigurationLoaderFile(configDir).load()

        return ConfigurationPack(configDir, forgeConf, dbConf)
    }

    private fun initLog(logger: Logger, configDir: String, logFilenamePrefix: String = "", serverNameSuffix: String = "") {
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


    private fun createDataSource(dbConf: DbConfiguration): ComboPooledDataSource {
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