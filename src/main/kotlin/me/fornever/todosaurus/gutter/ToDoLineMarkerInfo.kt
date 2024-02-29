package me.fornever.todosaurus.gutter

import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import me.fornever.todosaurus.TodosaurusBundle
import me.fornever.todosaurus.models.ToDoItem
import me.fornever.todosaurus.services.ToDoService
import javax.swing.Icon

class ToDoLineMarkerInfo(
    element: PsiElement,
) : MergeableLineMarkerInfo<PsiElement>(
    element,
    element.textRange,
    AllIcons.General.TodoDefault,
    ::tooltipProvider,
    /* navHandler = */ null,
    GutterIconRenderer.Alignment.LEFT,
    TodosaurusBundle.messagePointer("gutter-icon.accessible-name")
) {

    override fun canMergeWith(info: MergeableLineMarkerInfo<*>): Boolean = info is ToDoLineMarkerInfo
    override fun getCommonIcon(infos: MutableList<out MergeableLineMarkerInfo<*>>): Icon {
        return infos[0].icon
    }

    override fun createGutterRenderer() = ToDoGutterIconRenderer()
}

private fun tooltipProvider(element: PsiElement): String? {
    val items = ToDoService.getInstance(element.project).extractItems(element)
    val counts = countDifferentItems(items)
    return when {
        counts.linked == 0 && counts.new == 0 -> null
        counts.linked == 0 && counts.new == 1 -> TodosaurusBundle.message("gutter-icon.tooltip.new-item")
        counts.linked == 0 && counts.new > 1 -> TodosaurusBundle.message("gutter-icon.tooltip.new-items", counts.new)
        counts.linked == 1 && counts.new == 0 -> TodosaurusBundle.message("gutter-icon.tooltip.linked-item", (items.single() as ToDoItem.LinkedToDoItem).issueNumber)
        counts.linked > 1 && counts.new == 0 -> TodosaurusBundle.message("gutter-icon.tooltip.linked-items", counts.linked)
        else -> TodosaurusBundle.message("gutter-icon.tooltip.mixed-items", counts.new, counts.linked)
    }
}

private data class ItemCounts(val new: Int, val linked: Int)
private fun countDifferentItems(items: Sequence<ToDoItem>): ItemCounts {
    var new = 0
    var linked = 0
    for (item in items) {
        when (item) {
            is ToDoItem.NewToDoItem -> new++
            is ToDoItem.LinkedToDoItem -> linked++
        }
    }
    return ItemCounts(new, linked)
}
