package com.vladuken.plugin.mockkgenerator.template.context

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.liveTemplates.KotlinTemplateContextType

abstract class KotlinTestTemplateContextType(
    private val kotlinTemplateContextType: KotlinTemplateContextType,
    presentableName: String,
) : TemplateContextType(presentableName) {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return kotlinTemplateContextType.isInContext(templateActionContext) &&
                templateActionContext.isInTestDirectory()
    }

    private fun TemplateActionContext.isInTestDirectory(): Boolean {
        val virtualFile = file.viewProvider.virtualFile as? LightVirtualFile ?: return false
        val fullFilePath = virtualFile.originalFile?.path?.lowercase() ?: return false
        // Find project path to have relative to content root path
        val baseProjectPath = file.project.basePath.orEmpty().lowercase()
        // Clear to relative path
        val clearedFilePath = fullFilePath.replace(baseProjectPath, "")

        return clearedFilePath.contains("/test/") || clearedFilePath.contains("/tests/")
    }

    class Class : KotlinTestTemplateContextType(
        kotlinTemplateContextType = KotlinTemplateContextType.Class(),
        presentableName = "TEST_KOTLIN_CLASS",
    )

    class TopLevel : KotlinTestTemplateContextType(
        kotlinTemplateContextType = KotlinTemplateContextType.TopLevel(),
        presentableName = "TEST_KOTLIN_TOPLEVEL",
    )

    class Generic : KotlinTestTemplateContextType(
        kotlinTemplateContextType = KotlinTemplateContextType.Generic(),
        presentableName = "TEST_KOTLIN",
    )

    class ObjectDeclaration : KotlinTestTemplateContextType(
        kotlinTemplateContextType = KotlinTemplateContextType.ObjectDeclaration(),
        presentableName = "TEST_KOTLIN_OBJECT_DECLARATION",
    )
}