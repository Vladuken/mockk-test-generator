package com.vladuken.plugin.mockkgenerator.generator

import com.vladuken.plugin.mockkgenerator.model.GeneratorInputModel

/**
 * Generates string representation of test boilerplate class from provided [generatorInputModel]
 */
class MockKTestFileGenerator(
    private val generatorInputModel: GeneratorInputModel,
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
            generatorInputModel.mockFields.forEach {
                appendLine(it.name.asMockkTestField(it.type))
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
        return "@MockK\n" +
                "lateinit var $this : $type\n"
    }


    private fun StringBuilder.createPrepareForMethod(
        generatorInputModel: GeneratorInputModel,
    ) {
        val classConstructorName = generatorInputModel.className

        val thisLabel = if (generatorInputModel.useTestScope) {
            "this@${generatorInputModel.className}Test"
        } else {
            "this"
        }

        val optionalTestScopeReceiver = "TestScope.".takeIf { generatorInputModel.useTestScope } ?: ""
        val parametersOfMethods = generatorInputModel.mockFields.joinToString("\n") {
            it.name + " : " + it.type + " = $thisLabel." + it.name + ","
        }
        val parametersOfConstructor = generatorInputModel.mockFields.joinToString("\n") {
            it.name + " = " + it.wrappedNameIfNeeded + ","
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

}




