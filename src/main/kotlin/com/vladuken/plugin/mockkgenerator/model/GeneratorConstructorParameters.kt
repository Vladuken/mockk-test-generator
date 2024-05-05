package com.vladuken.plugin.mockkgenerator.model

/**
 * Represents parameter in constructor of class that will be used for test boilerplate generation.
 * @param name if string of declared title of field
 * @param type is string of type of field.
 * @param wrappedNameIfNeeded is [name] with additional changes if needed. Passed to constructor of class.
 */
data class GeneratorConstructorParameters(
    val name: String,
    val type: String,
    val wrappedNameIfNeeded: String,
)
