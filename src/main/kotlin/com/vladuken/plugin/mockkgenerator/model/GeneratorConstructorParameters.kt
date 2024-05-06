package com.vladuken.plugin.mockkgenerator.model

/**
 * Represents parameter in constructor of class that will be used for test boilerplate generation.
 * @param isPrimitive shows is type of parameter is primitive (Int,Short,Byte...)
 * @param methodParamName is human-readable name of parameter, the way it was named in constructor
 * @param methodDefaultParameterName if default value string for generated prepare() method.
 * @param shortType is string of type of field.
 * @param methodParamWrappedNameIfNeeded is [methodParamName] with additional changes if needed. Passed to constructor of class.
 */
data class GeneratorConstructorParameters(
    val isPrimitive: Boolean,
    val methodParamName: String,
    val methodDefaultParameterName: String,
    val shortType: String,
    val methodParamWrappedNameIfNeeded: String,
)
