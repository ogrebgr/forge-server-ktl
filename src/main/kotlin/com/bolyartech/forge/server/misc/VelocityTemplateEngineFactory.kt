package com.bolyartech.forge.server.misc

import org.apache.velocity.app.VelocityEngine
import java.util.*

class VelocityTemplateEngineFactory @JvmOverloads constructor(
    templatePathPrefix: String,
    additionalSettings: Map<String, String> = HashMap()
) : TemplateEngineFactory {
    private val velocityEngine: VelocityEngine
    private val templatePathPrefix: String
    override fun createNew(): TemplateEngine {
        return VelocityTemplateEngine(velocityEngine, templatePathPrefix)
    }

    init {
        velocityEngine = VelocityEngine()
        val properties = Properties()
        properties.setProperty("resource.loaders", "class")
        properties.setProperty(
            "resource.loader.class.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader"
        )
        for (key in additionalSettings.keys) {
            properties.setProperty(key, additionalSettings[key])
        }
        velocityEngine.init(properties)
        this.templatePathPrefix = templatePathPrefix
    }
}