package com.bolyartech.forge.server.misc

/**
 * Template engine interface
 */
interface TemplateEngine {
    /**
     * Assign value to a variable name
     * If variable already assigned, value will be overwritten
     *
     * @param varName Variable name
     * @param obj  Value
     */
    fun assign(varName: String, obj: Any)

    /**
     * Assigns boolean `true` to a variable
     * If variable already assigned, value will be overwritten
     *
     * @param varName Variable name
     */
    fun assign(varName: String)

    /**
     * Alias of [.assign]
     */
    fun export(varName: String, obj: Any)

    /**
     * Alias of [.export]
     */
    fun export(varName: String)

    /**
     * Renders a template to string
     *
     * @param templateName Template name
     * @return Rendered string, usually HTML
     */
    fun render(templateName: String): String

    /**
     * Checks if the variable name varName already has assigned value
     *
     * @param varName name of the variable
     * @return true if the variable is already assigned
     */
    fun isAssigned(varName: String): Boolean
}