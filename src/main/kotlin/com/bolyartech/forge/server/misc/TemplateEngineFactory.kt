package com.bolyartech.forge.server.misc

/**
 * Template Engine Factory interface
 */
interface TemplateEngineFactory {
    /**
     * Creates new [TemplateEngine]
     *
     * @return Template engine
     */
    fun createNew(): TemplateEngine
}