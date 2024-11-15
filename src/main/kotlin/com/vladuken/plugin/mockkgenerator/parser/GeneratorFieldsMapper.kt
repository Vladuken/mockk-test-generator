package com.vladuken.plugin.mockkgenerator.parser

import com.vladuken.plugin.mockkgenerator.model.DomainClassInfo
import com.vladuken.plugin.mockkgenerator.model.GeneratorConstructorParameters
import com.vladuken.plugin.mockkgenerator.model.GeneratorInputModel

private const val DAGGER_LAZY_FQN = "dagger.Lazy"
private const val KOTLIN_LAZY_FQN = "kotlin.Lazy"

private const val TEST_SCOPE_IMPORT = "kotlinx.coroutines.test.TestScope"
private const val MOCKK_SCOPE_IMPORT = "io.mockk.impl.annotations.MockK"
private const val MOCKK_ANNOTATIONS_IMPORT = "io.mockk.MockKAnnotations"
private const val MOCKK_UNMOCKK_ALL_IMPORT = "io.mockk.unmockkAll"

private const val JUNIT_BEFORE_IMPORT = "org.junit.Before"
private const val JUNIT_AFTER_IMPORT = "org.junit.After"

private val primitivesMapWithDefaultValues = mapOf(
    "kotlin.Int" to "0",
    "kotlin.Long" to "0L",
    "kotlin.Double" to "0.0",
    "kotlin.Float" to "0.0F",
    "kotlin.String" to "\"\"",
    "kotlin.Boolean" to "false",
    "kotlin.Byte" to "0",
    "kotlin.Short" to "0",
)

/**
 * Create [GeneratorInputModel] from provided [DomainClassInfo] and additional parameters
 */
fun DomainClassInfo.mapToGeneratorModel(
    generateOverTestScope: Boolean,
    generateBefore: Boolean,
    generateAfter: Boolean,
): GeneratorInputModel {
    val optionalImports = setOfNotNull(
        // TestScope dependency
        TEST_SCOPE_IMPORT.takeIf { generateOverTestScope },
        // @Mockk dependency
        MOCKK_SCOPE_IMPORT.takeIf { parameters.isNotEmpty() },
        // @Before
        JUNIT_BEFORE_IMPORT.takeIf { generateBefore },
        MOCKK_ANNOTATIONS_IMPORT.takeIf { generateBefore },
        // @After
        JUNIT_AFTER_IMPORT.takeIf { generateAfter },
        MOCKK_UNMOCKK_ALL_IMPORT.takeIf { generateAfter },
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
                isPrimitive = it.parameterTypeFqn in primitivesMapWithDefaultValues.keys,
                methodDefaultParameterName = primitivesMapWithDefaultValues[it.parameterTypeFqn] ?: it.parameterName,
                methodParamName = it.parameterName,
                shortType = when (it.parameterTypeFqn) {
                    KOTLIN_LAZY_FQN -> it.nestedGenericTypeName
                    DAGGER_LAZY_FQN -> it.nestedGenericTypeName
                    else -> it.rawTypeString
                },
                methodParamWrappedNameIfNeeded = when (it.parameterTypeFqn) {
                    KOTLIN_LAZY_FQN -> "lazyOf(${it.parameterName})"
                    DAGGER_LAZY_FQN -> "{${it.parameterName}}"
                    else -> it.parameterName
                }
            )
            add(parametersModel)
        }
    }
}