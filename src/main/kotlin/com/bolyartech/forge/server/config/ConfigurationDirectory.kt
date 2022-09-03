package com.bolyartech.forge.server.config

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import org.slf4j.LoggerFactory
import java.nio.file.FileSystem
import java.nio.file.Path
import kotlin.io.path.*


private const val CLI_PARAM_CONFIG_DIR = "config-dir"
private const val CONFIG_DIR = "conf"

fun detectConfigurationDirectory(fs: FileSystem, cliArgs: Array<String>): Path? {
    val logger = LoggerFactory.getLogger("com.bolyartech.forge.server.config")

    val cmd: CommandLine = parseCommandLine(cliArgs)
    val confDirCandidateStr = cmd.getOptionValue(CLI_PARAM_CONFIG_DIR)
    if (confDirCandidateStr != null) {
        val confDirCandidate = fs.getPath(confDirCandidateStr)
        if (!confDirCandidate.isDirectory()) {
            logger.error("Value of parameter $CLI_PARAM_CONFIG_DIR is not directory ($confDirCandidateStr)")
            return null
        }
        val forgeConfPath = fs.getPath(confDirCandidateStr, ForgeServerConfigurationLoaderFile.FORGE_CONF_FILENAME)
        if (!forgeConfPath.exists()) {
            logger.error("Cannot find ${ForgeServerConfigurationLoaderFile.FORGE_CONF_FILENAME} in $confDirCandidate")
            return null
        }

        return if (isForgeConfPresent(fs, confDirCandidate)) {
            confDirCandidate
        } else {
            null
        }

    } else {
        val configDir = fs.getPath(".", CONFIG_DIR)
        return if (configDir.exists() && configDir.isDirectory() && configDir.isReadable()) {
            if (isForgeConfPresent(fs, configDir)) {
                configDir.normalize().absolute()
            } else {
                null
            }
        } else {
            null
        }
    }
}

private fun isForgeConfPresent(fs: FileSystem, dir: Path): Boolean {
    val forgeConfPath = fs.getPath(dir.pathString, ForgeServerConfigurationLoaderFile.FORGE_CONF_FILENAME)
    if (!forgeConfPath.exists()) {
        return false
    }

    return forgeConfPath.isRegularFile()
}

private fun parseCommandLine(args: Array<String>): CommandLine {
    val argsParser = DefaultParser()

    return argsParser.parse(createCliArgOptions(), args)
}

fun createCliArgOptions(): Options {
    val cliOptions = Options()
    cliOptions.addOption("c", CLI_PARAM_CONFIG_DIR, true, "Path to configuration directory, i.e. where forge.conf resides.")
    return cliOptions
}