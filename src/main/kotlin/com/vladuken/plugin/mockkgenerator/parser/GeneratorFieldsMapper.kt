package com.vladuken.plugin.mockkgenerator.parser

import com.vladuken.plugin.mockkgenerator.model.DomainClassInfo
import com.vladuken.plugin.mockkgenerator.model.GeneratorConstructorParameters
import com.vladuken.plugin.mockkgenerator.model.GeneratorInputModel

private const val DAGGER_LAZY_FQN = "dagger.Lazy"
private const val KOTLIN_LAZY_FQN = "kotlin.Lazy"

private const val TEST_SCOPE_IMPORT = "kotlinx.coroutines.test.TestScope"
private const val MOCKK_SCOPE_IMPORT = "io.mockk.impl.annotations.MockK"

/**
 * Create [GeneratorInputModel] from provided [DomainClassInfo] and additional parameters
 */
fun DomainClassInfo.mapToGeneratorModel(
    generateOverTestScope: Boolean,
): GeneratorInputModel {
    val optionalImports = setOfNotNull(
        TEST_SCOPE_IMPORT.takeIf { generateOverTestScope },
        MOCKK_SCOPE_IMPORT.takeIf { parameters.isNotEmpty() },
    )
    val imports = toImports() + optionalImports
    val mockFields = toMockkConstructorParameters()
    return GeneratorInputModel(
        useTestScope = generateOverTestScope,
        packageName = packageName,
        className = className,
        imports = imports,
        mockFields = mockFields,
    )
}

/**
 * Returns all imports that will be used in this file.
 */
private fun DomainClassInfo.toImports(): Set<String> {
    return buildSet {
        add(fqClassName)
        parameters.forEach { parameter ->
            add(parameter.parameterTypeFqn)
            addAll(parameter.nestedGenericTypesFqn)
        }
    }
}


/**
 * Return all info that will be used for @Mockk parameters generation.
 */
private fun DomainClassInfo.toMockkConstructorParameters(): List<GeneratorConstructorParameters> {
    return buildList {
        parameters.forEach {
            val parametersModel = GeneratorConstructorParameters(
                name = it.parameterName,
                type = when (it.parameterTypeFqn) {
                    KOTLIN_LAZY_FQN -> it.nestedGenericTypeName
                    DAGGER_LAZY_FQN -> it.nestedGenericTypeName
                    else -> it.rawTypeString
                },
                wrappedNameIfNeeded = when (it.parameterTypeFqn) {
                    KOTLIN_LAZY_FQN -> "lazyOf(${it.parameterName})"
                    DAGGER_LAZY_FQN -> "{${it.parameterName}}"
                    else -> it.parameterName
                }
            )
            add(parametersModel)
        }
    }
}