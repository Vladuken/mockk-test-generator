package com.vladuken.plugin.mockkgenerator.ui

import com.intellij.openapi.ui.DialogWrapper
import com.vladuken.plugin.mockkgenerator.utils.TextConstants
import java.awt.BorderLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Separate dialog for setting some options for test file generation.
 *
 */
class MockkGeneratorSettingsDialog(
    private var _shouldUseTestScope: Boolean = false,
) : DialogWrapper(true) {

    init {
        title = TextConstants.settings_dialog_title
        init()
    }

    val shouldUseTestScope: Boolean get() = _shouldUseTestScope

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(BorderLayout())
        val checkBox = JCheckBox(TextConstants.settings_dialog_checkbox_test_scope_message, _shouldUseTestScope)
        checkBox.addChangeListener { _shouldUseTestScope = checkBox.isSelected }
        dialogPanel.add(checkBox, BorderLayout.CENTER)

        return dialogPanel
    }
}