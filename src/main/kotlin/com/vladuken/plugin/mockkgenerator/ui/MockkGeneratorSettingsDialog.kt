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
    private var _generateBefore: Boolean = true,
    private var _generateAfter: Boolean = false,
) : DialogWrapper(true) {

    init {
        title = TextConstants.settings_dialog_title
        init()
    }

    val shouldUseTestScope: Boolean get() = _shouldUseTestScope

    val relaxUnitFunByDefault: Boolean get() = _relaxUnitFunByDefault

    val relaxedByDefault: Boolean get() = _relaxedByDefault

    val generateBefore: Boolean get() = _generateBefore
    val generateAfter: Boolean get() = _generateAfter

    override fun createCenterPanel(): JComponent {
        val dialogPanel = JPanel(
            /* layout = */ ListLayout.vertical(
                vertGap = 6,
                horAlignment = Alignment.START,
                horGrow = GrowPolicy.GROW,
            )
        )


        // TestScope Ext
        dialogPanel.add(
            createCheckBox(
                message = TextConstants.settings_dialog_checkbox_test_scope_message,
                defaultValue = _shouldUseTestScope,
                onChange = { isSelected -> _shouldUseTestScope = isSelected }
            )
        )

        dialogPanel.add(JSeparator())

        // Relax Unit Fun
        dialogPanel.add(
            createCheckBox(
                message = TextConstants.settings_dialog_checkbox_relax_unit_fun,
                defaultValue = _relaxUnitFunByDefault,
                onChange = { isSelected -> _relaxUnitFunByDefault = isSelected }
            )
        )

        // Relax All
        dialogPanel.add(
            createCheckBox(
                message = TextConstants.settings_dialog_checkbox_relax,
                defaultValue = _relaxUnitFunByDefault,
                onChange = { isSelected -> _relaxUnitFunByDefault = isSelected }
            )
        )

        dialogPanel.add(JSeparator())
        dialogPanel.add(
            createCheckBox(
                message = TextConstants.settings_dialog_checkbox_before_method,
                defaultValue = _generateBefore,
                onChange = { isSelected -> _generateBefore = isSelected }
            )
        )

        dialogPanel.add(
            createCheckBox(
                message = TextConstants.settings_dialog_checkbox_after_method,
                defaultValue = _generateAfter,
                onChange = { isSelected -> _generateAfter = isSelected }
            )
        )
        dialogPanel.add(JSeparator())


        return dialogPanel
    }

    private fun createCheckBox(
        message: String,
        defaultValue: Boolean,
        onChange: (Boolean) -> Unit,
    ): JCheckBox {
        val createdCheckBox = JCheckBox(message, defaultValue)
        createdCheckBox.addChangeListener { onChange(createdCheckBox.isSelected) }
        return createdCheckBox
    }
}