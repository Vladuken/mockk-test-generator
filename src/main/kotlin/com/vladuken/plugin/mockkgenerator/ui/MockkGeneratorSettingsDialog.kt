package com.vladuken.plugin.mockkgenerator.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.panels.ListLayout
import com.intellij.ui.components.panels.ListLayout.Alignment
import com.intellij.ui.components.panels.ListLayout.GrowPolicy
import com.vladuken.plugin.mockkgenerator.utils.TextConstants
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSeparator

/**
 * Separate dialog for setting some options for test file generation.
 *
 */
@Suppress("UnstableApiUsage")
class MockkGeneratorSettingsDialog(
    private var _shouldUseTestScope: Boolean = false,
    private var _relaxUnitFunByDefault: Boolean = false,
    private var _relaxedByDefault: Boolean = false,
) : DialogWrapper(true) {

    init {
        title = TextConstants.settings_dialog_title
        init()
    }

    val shouldUseTestScope: Boolean get() = _shouldUseTestScope

    val relaxUnitFunByDefault: Boolean get() = _relaxUnitFunByDefault

    val relaxedByDefault: Boolean get() = _relaxedByDefault

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(
            /* layout = */ ListLayout.vertical(
                vertGap = 6,
                horAlignment = Alignment.START,
                horGrow = GrowPolicy.GROW,
            )
        )


        // TestScope Ext
        val testScopeExtCheckBox =
            JCheckBox(TextConstants.settings_dialog_checkbox_test_scope_message, _shouldUseTestScope)
        testScopeExtCheckBox.addChangeListener { _shouldUseTestScope = testScopeExtCheckBox.isSelected }
        dialogPanel.add(testScopeExtCheckBox)

        dialogPanel.add(JSeparator())

        // Relax Unit Fun
        val relaxUnitFunCheckBox =
            JCheckBox(TextConstants.settings_dialog_checkbox_relax_unit_fun, _relaxUnitFunByDefault)
        relaxUnitFunCheckBox.addChangeListener { _relaxUnitFunByDefault = relaxUnitFunCheckBox.isSelected }
        dialogPanel.add(relaxUnitFunCheckBox)

        // Relax All
        val relaxCheckBox = JCheckBox(TextConstants.settings_dialog_checkbox_relax, _relaxUnitFunByDefault)
        relaxCheckBox.addChangeListener { _relaxedByDefault = relaxCheckBox.isSelected }
        dialogPanel.add(relaxCheckBox)

        dialogPanel.add(JSeparator())


        return dialogPanel
    }
}