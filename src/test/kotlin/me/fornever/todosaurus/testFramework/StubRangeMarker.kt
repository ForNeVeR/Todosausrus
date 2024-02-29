package me.fornever.todosaurus.testFramework

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.util.Key

class StubRangeMarker : RangeMarker {
    override fun <T : Any?> getUserData(key: Key<T>): T = error("Not implemented")
    override fun <T : Any?> putUserData(key: Key<T>, value: T?) = error("Not implemented")
    override fun getStartOffset(): Int = error("Not implemented")
    override fun getEndOffset(): Int = error("Not implemented")
    override fun getDocument(): Document = error("Not implemented")
    override fun isValid(): Boolean = error("Not implemented")
    override fun setGreedyToLeft(greedy: Boolean) = error("Not implemented")
    override fun setGreedyToRight(greedy: Boolean) = error("Not implemented")
    override fun isGreedyToRight(): Boolean = error("Not implemented")
    override fun isGreedyToLeft(): Boolean = error("Not implemented")
    override fun dispose() {}
}
