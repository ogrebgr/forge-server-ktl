package com.bolyartech.forge.server

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import com.bolyartech.forge.server.config.ForgeConfigurationException
import com.bolyartech.forge.server.config.ForgeServerConfiguration
import com.bolyartech.forge.server.config.ForgeServerConfigurationLoaderFile
import com.bolyartech.forge.server.config.detectConfigurationDirectory
import com.bolyartech.forge.server.db.HikariCpDbConfiguration
import com.bolyartech.forge.server.db.HikariCpDbConfigurationLoaderFile
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import javax.sql.DataSource
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.system.exitProcess


interface ForgeServer {
    /**
     * @return `true` if Forge server is started successfully, `false` otherwise.
     */
    @Throws(ForgeConfigurationException::class)
    fun start(configurationPack: ConfigurationPack, fileSystem: FileSystem): Boolean
    fun shutdown()

    fun onStart()
    fun onBeforeWebServerStart()
    fun onAfterWebServerStart(webServerStopper: WebServerStopper)

    fun createWebServer(
        forgeConfig: ConfigurationPack,
        fileSystem: FileSystem
    ): WebServer

    fun testDbConnection()

    fun getInstrumentationReader(): WebServerInstrumentationReader

    data class ConfigurationPack(
        val configurationDirectory: Path,
        val forgeServerConfiguration: ForgeServerConfiguration,
        val dbConfiguration: HikariCpDbConfiguration,
    )

    companion object {
        fun createDataSourceHelper(dbConf: HikariCpDbConfiguration): DataSource {
            val config = HikariConfig()
            config.jdbcUrl = dbConf.dbDsn
            config.username = dbConf.dbUsername
            config.password = dbConf.dbPassword
            if (dbConf.minPoolSize != null) {
                config.minimumIdle = dbConf.minPoolSize
            }
            config.maximumPoolSize = dbConf.maxPoolSize
            if (dbConf.leakDetectionThresholdMillis != null) {
                config.leakDetectionThreshold = dbConf.leakDetectionThresholdMillis
            }
            config.transactionIsolation = "TRANSACTION_READ_COMMITTED"
            config.addDataSourceProperty("cachePrepStmts", "true")
            config.addDataSourceProperty("prepStmtCacheSize", dbConf.prepStmtCacheSize)
            config.addDataSourceProperty("prepStmtCacheSqlLimit", dbConf.prepStmtCacheSqlLimit)

            return HikariDataSource(config)
        }

        fun loadConfigurationPack(fs: FileSystem, args: Array<String>): ConfigurationPack {
            val filesystem = FileSystems.getDefault()
            val configDir = detectConfigurationDirectory(filesystem, args)
                ?: throw ForgeConfigurationException("Cannot detect the configuration directory. Exiting.")

            val forgeConf = ForgeServerConfigurationLoaderFile(configDir).load()
            val dbConf = HikariCpDbConfigurationLoaderFile(configDir).load()

            return ConfigurationPack(configDir, forgeConf, dbConf)
        }

        fun initLog(
            logger: Logger,
            configDir: String,
            logFilenamePrefix: String = "",
            serverNameSuffix: String = "",
            logbackXmlFile: String? = null
        ) {
            val context = LoggerFactory.getILoggerFactory() as LoggerContext
            val jc = JoranConfigurator()
            jc.context = context
            context.reset()

            context.putProperty("application-name", logFilenamePrefix + serverNameSuffix)

            val logbackXmlFileFinal = logbackXmlFile ?: "logback.xml"

            val f = File(configDir, logbackXmlFileFinal)

            println()
            println("Will try logback config: " + f.absolutePath)
            println()

            if (f.exists()) {
                val logbackConfigFilePath = f.absolutePath
                try {
                    jc.doConfigure(logbackConfigFilePath)
                    logger.info("+++ logback initialized OK ($logbackConfigFilePath)")
                } catch (e: JoranException) {
                    e.printStackTrace()
                }
            } else {
                println("!!! No logback configuration file found. Using default configuration.")
            }
        }

        fun initLogger(
            logger: Logger,
            configDir: Path,
            variantSuffix: String,
            logLinePrefix: String = "",
            serverNameSuffix: String = ""
        ) {
            val variantSuffixFinal = variantSuffix.lowercase()
            val f = File(configDir.pathString, "logback-$variantSuffixFinal.xml")
            val logbackXmlFile = if (f.exists()) {
                if (!f.isFile) {
                    logger.error("logback configuration file is not a file: ${f.absolutePath}")
                    exitProcess(1)
                }

                f.name
            } else {
                "logback.xml"
            }


            initLog(logger, configDir.pathString, logLinePrefix, serverNameSuffix, logbackXmlFile)
        }
    }
}


abstract class AbstractForgeServer : ForgeServer {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private var isStarted = false
    private var isShutdown = false

    private lateinit var config: ForgeServer.ConfigurationPack
    private var fileSystem: FileSystem? = null

    private var webServer: WebServer? = null


    @Override
    override fun start(configurationPack: ForgeServer.ConfigurationPack, fileSystem: FileSystem): Boolean {
        require(!isStarted)
        require(!isShutdown)

        logger.debug("+++ Forge server starting...")

        onStart()

        config = configurationPack
        this.fileSystem = fileSystem

        if (config.forgeServerConfiguration.uploadsDirectory.isNotEmpty()) {
            val ulDir = this.fileSystem!!.getPath(config.forgeServerConfiguration.uploadsDirectory)
            if (!ulDir.exists()) {
                logger.error("Uploads dir specified in forge.conf (${ulDir.pathString}) does not exists. Leave blank if not used.")
                return false
            }
        }

        if (config.forgeServerConfiguration.downloadsDirectory.isNotEmpty()) {
            val dlDir = this.fileSystem!!.getPath(config.forgeServerConfiguration.downloadsDirectory)
            if (!dlDir.exists()) {
                logger.error("Downloads dir specified in forge.conf (${dlDir.pathString}) does not exists. Leave blank if not used.")
                return false
            }
        }

        onBeforeWebServerStart()
        webServer = createWebServer(config, fileSystem)
        testDbConnection()
        if (!webServer!!.start()) {
            logger.warn("Web server failed to start, so we abort Forge server start.")
            return false
        }

        onAfterWebServerStart(webServer!!)

        isStarted = true

        logger.debug("+++ Forge server started OK")
        logger.info("Deployment: ${config.forgeServerConfiguration.deployment}")

        return true
    }

    override fun getInstrumentationReader(): WebServerInstrumentationReader {
        require(isStarted)
        return webServer!!.getInstrumentation()
    }

    @Override
    override fun shutdown() {
        require(isStarted)
        logger.debug("--- Shutting down forge server...")

        isStarted = false
        isShutdown = true

        webServer?.stop()

        logger.debug("--- Forge server shut down successfully.")
    }
}


abstract class AbstractForgeServerAdapter : AbstractForgeServer() {
    override fun onStart() {
    }

    override fun onBeforeWebServerStart() {
    }

    override fun onAfterWebServerStart(webServerStopper: WebServerStopper) {
    }
}