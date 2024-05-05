package com.vladuken.plugin.mockkgenerator.model

/**
 * Clear representation of all needed data for test generation
 * @param packageName is package that contains this class
 * @param className is name of class
 * @param fqClassName is FQN version of [className]
 * @param parameters is list of [DomainConstructorParameters] that was provided in constructor of class.
 */
data class DomainClassInfo(
    val packageName: String,
    val className: String,
    val fqClassName: String,
    val parameters: List<DomainConstructorParameters>,
)
