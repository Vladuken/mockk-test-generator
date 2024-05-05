package com.vladuken.plugin.mockkgenerator.model


/**
 * Represents clean model that will be used in generation of test body.
 *
 * @param useTestScope determine whether [prepare***()] method will be an ext of coroutine TestScope.
 */
data class GeneratorInputModel(
    val useTestScope: Boolean,
    val packageName: String,
    val imports: Set<String>,
    val className: String,
    val mockFields: List<GeneratorConstructorParameters>,
)