package com.vladuken.plugin.mockkgenerator


import com.intellij.codeInsight.actions.OptimizeImportsProcessor
import com.intellij.codeInsight.actions.ReformatCodeProcessor
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.findTopmostParentOfType
import com.intellij.testIntegration.createTest.CreateTestUtils
import com.intellij.ui.awt.RelativePoint
import com.vladuken.plugin.mockkgenerator.generator.MockKTestFileGenerator
import com.vladuken.plugin.mockkgenerator.parser.mapToGeneratorModel
import com.vladuken.plugin.mockkgenerator.parser.parseToDomainClassInfo
import com.vladuken.plugin.mockkgenerator.ui.MockkGeneratorSettingsDialog
import com.vladuken.plugin.mockkgenerator.utils.TextConstants
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.psi.KtClass

/**
 * Will generate test file with boilerplate code for @MockK library.
 */
class MockKBoilerplateGeneratorAction : AnAction() {

    override fun update(e: AnActionEvent) {
        val ktClass = getKtClassFromContext(e)
        val isEnabled = when {
            ktClass == null -> false
            ktClass.isEnum() -> false
            ktClass.isInterface() -> false
            ktClass.isValue() -> false
            ktClass.isAnnotation() -> false
            ktClass.isData() -> false
            else -> true
        }
        e.presentation.isEnabled = isEnabled
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val ktClass = getKtClassFromContext(e) ?: return
        val project = e.project ?: return

        // Show dialog with settings
        val pluginSettingsDialog = MockkGeneratorSettingsDialog()
        if (pluginSettingsDialog.showAndGet().not()) return

        val shouldBeGeneratedOverTestScope = pluginSettingsDialog.shouldUseTestScope

        // Select directory of where to add generated file
        val selectedDirectory = CreateTestUtils.selectTargetDirectory(
            /* packageName = */ "",
            /* project = */ project,
            /* targetModule = */ ktClass.module,
        ) ?: return

        // Parse class info into model
        val domainClassInfo = ktClass.parseToDomainClassInfo()

        // Generate boilerplate file body string
        val generatorInput = domainClassInfo.mapToGeneratorModel(
            generateOverTestScope = shouldBeGeneratedOverTestScope,
        )
        val generatedTestFileBody = MockKTestFileGenerator(generatorInput).buildFileStringRepresentation()

        // Run Write action with creation of file
        ApplicationManager.getApplication().runWriteAction {

            // Create Test File and add it to selected directory
            val createdFile: PsiFile = createKotlinFileFromText(
                title = domainClassInfo.className + "Test.kt",
                text = generatedTestFileBody,
                project = project,
            )

            // Add test file to directory or notify that its already created
            val alreadyCreatedFile = selectedDirectory.findFile(createdFile.name)
            val addedFile = if (alreadyCreatedFile != null) {
                // Open created file in IDE
                PsiNavigationSupport.getInstance().createNavigatable(
                    /* project = */ project,
                    /* vFile = */ alreadyCreatedFile.virtualFile,
                    /* offset = */ alreadyCreatedFile.textOffset,
                )
                    .navigate(true)
                showErrorNotification(project)
                return@runWriteAction
            } else {
                selectedDirectory.add(createdFile) as? PsiFile ?: return@runWriteAction
            }

            // Format generated code
            ReformatCodeProcessor(
                /* file = */ addedFile,
                /* processChangedTextOnly = */ false,
            ).run()
            // Optimize imports in file
            OptimizeImportsProcessor(
                /* project = */ project,
                /* file = */ addedFile,
            ).run()
            // Open created file in IDE
            PsiNavigationSupport.getInstance().createNavigatable(
                /* project = */ project,
                /* vFile = */ addedFile.virtualFile,
                /* offset = */ addedFile.textOffset,
            )
                .navigate(true)
        }
    }
}


private fun getKtClassFromContext(e: AnActionEvent): KtClass? {
    val psiFile = e.getData(LangDataKeys.PSI_FILE)
    val editor: Editor? = e.getData(PlatformDataKeys.EDITOR)

    if (psiFile == null || editor == null) return null

    val offset: Int = editor.caretModel.offset
    val element = psiFile.findElementAt(offset)

    return element?.findTopmostParentOfType<KtClass>()
}

private fun createKotlinFileFromText(
    title: String,
    text: String,
    project: Project,
): PsiFile = PsiFileFactory.getInstance(project)
    .createFileFromText(
        /* name = */ title,
        /* language = */ KotlinLanguage.INSTANCE,
        /* text = */ text,
        /* eventSystemEnabled = */ false,
        /* markAsCopy = */ true,
    )


fun showErrorNotification(project: Project) {
    // notification group for non-sticky balloon notifications
    val statusBar: StatusBar = WindowManager.getInstance().getStatusBar(project)
    val statusBarComponent = statusBar.component ?: return

    JBPopupFactory.getInstance()
        .createHtmlTextBalloonBuilder(TextConstants.file_already_created, MessageType.ERROR, null)
        .setFadeoutTime(4000)
        .createBalloon()
        .show(
            /* target = */ RelativePoint.getCenterOf(statusBarComponent),
            /* preferredPosition = */ Balloon.Position.atRight,
        )
}
