package me.fornever.todosaurus.gutter

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.editor.markup.GutterIconRenderer
import javax.swing.Icon

class ToDoGutterIconRenderer : GutterIconRenderer() {
    override fun equals(other: Any?) = other is ToDoGutterIconRenderer
    override fun hashCode() = 0

    override fun getIcon(): Icon = AllIcons.General.TodoDefault
    override fun getPopupMenuActions(): ActionGroup? {
        TODO()
    }

    override fun isDumbAware() = true
}
