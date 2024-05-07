package com.vladuken.plugin.mockkgenerator.generator

import com.vladuken.plugin.mockkgenerator.model.GeneratorConstructorParameters
import com.vladuken.plugin.mockkgenerator.model.GeneratorInputModel

/**
 * Generates string representation of test boilerplate class from provided [generatorInputModel]
 */
class MockKTestFileGenerator(
    private val generatorInputModel: GeneratorInputModel,
    private val mockKRelaxUnitFun: Boolean,
    private val mockKRelaxAll: Boolean,
    private val generateBefore: Boolean,
    private val generateAfter: Boolean,
) {

    fun buildFileStringRepresentation(): String {
        return buildString {
            // package name
            appendLine(generatorInputModel.packageName.asPackageLine())
            appendLine()
            // imports
            generatorInputModel.imports.sortedBy { it }.forEach { appendLine(it.asImport()) }
            appendLine()
            // create class
            appendLine(generatorInputModel.className.asTestClassDeclaration())
            appendLine()
            // create @Mockk fields
            generatorInputModel.mockFields
                .filterNot { it.isPrimitive }
                .forEach {
                    appendLine(it.methodParamName.asMockkTestField(it.shortType))
                }

            // create before method
            if (generateBefore) {
                createBeforeMethod()
            }

            // create after method
            if (generateAfter) {
                createAfterMethod()
            }
            // create prepareForMethod
            createPrepareForMethod(generatorInputModel)

            appendLine()
            appendLine("}")
        }
    }

    private fun String.asPackageLine(): String {
        return "package $this"
    }

    private fun String.asImport(): String {
        return "import $this"
    }

    private fun String.asTestClassDeclaration(): String {
        return "class " + this + "Test {"
    }

    private fun String.asMockkTestField(type: String): String {
        val mockkAnnotation = when {
            mockKRelaxAll && mockKRelaxUnitFun -> """
                @MockK(
                    relaxed = true,
                    relaxUnitFun = true,
                )
            """.trimIndent()

            mockKRelaxAll -> "@MockK(relaxed = true)"
            mockKRelaxUnitFun -> "@MockK(relaxUnitFun = true)"
            else -> "@MockK"
        }
        return "$mockkAnnotation\n" +
                "lateinit var $this : $type\n"
    }


    private fun StringBuilder.createBeforeMethod() {
        val beforeMethodString = """
            @Before
            fun before() {
                MockKAnnotations.init(this)
            }
        """.trimIndent()

        appendLine(beforeMethodString)
    }

    private fun StringBuilder.createAfterMethod() {
        val afterMethodString = """
            @After
            fun after() {
                unmockkAll()
            }
        """.trimIndent()

        appendLine(afterMethodString)
    }


    private fun StringBuilder.createPrepareForMethod(
        generatorInputModel: GeneratorInputModel,
    ) {
        val classConstructorName = generatorInputModel.className

        val optionalTestScopeReceiver = "TestScope.".takeIf { generatorInputModel.useTestScope } ?: ""
        val parametersOfMethods = generatorInputModel.mockFields.joinToString("\n") { field ->
            val thisLabel = prepareThisLabelIfNeeded(field)
            field.methodParamName + " : " + field.shortType + " = $thisLabel" + field.methodDefaultParameterName + ","
        }
        val parametersOfConstructor = generatorInputModel.mockFields.joinToString("\n") { field ->
            field.methodParamName + " = " + field.methodParamWrappedNameIfNeeded + ","
        }
        val functionMethodLine = """
        private fun ${optionalTestScopeReceiver}prepare${classConstructorName}(
            $parametersOfMethods
        ):${classConstructorName} {
            return $classConstructorName(
                $parametersOfConstructor
            )
        }
    """.trimIndent()
        appendLine(functionMethodLine)
    }


    private fun prepareThisLabelIfNeeded(
        generatorConstructorParameters: GeneratorConstructorParameters,
    ): String {
        val thisLabel = if (generatorInputModel.useTestScope) {
            "this@${generatorInputModel.className}Test."
        } else {
            "this."
        }
        val finalThisLabel = thisLabel.takeIf { generatorConstructorParameters.isPrimitive.not() } ?: ""

        return finalThisLabel
    }

}





