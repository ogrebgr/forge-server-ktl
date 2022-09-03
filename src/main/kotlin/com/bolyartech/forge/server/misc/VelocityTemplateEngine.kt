package com.bolyartech.forge.server.misc

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.StringWriter

class VelocityTemplateEngine(private val velocityEngine: VelocityEngine, templatePathPrefix: String) :
    TemplateEngine {
    private val velocityContext: VelocityContext = VelocityContext()
    private val templatePathPrefix: String
    override fun assign(varName: String, `object`: Any) {
        velocityContext.put(varName, `object`)
    }

    override fun assign(varName: String) {
        assign(varName, true)
    }

    override fun export(varName: String, `object`: Any) {
        assign(varName, `object`)
    }

    override fun export(varName: String) {
        assign(varName)
    }

    override fun render(templateName: String): String {
        val sw = StringWriter()
        val t = velocityEngine.getTemplate(templatePathPrefix + templateName, "UTF-8")
        t.merge(velocityContext, sw)
        return sw.toString()
    }

    init {
        this.templatePathPrefix = templatePathPrefix
    }
}