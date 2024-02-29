package me.fornever.todosaurus.models

sealed class ToDoItem {
    class NewToDoItem(val text: String) : ToDoItem()
    class LinkedToDoItem(val issueNumber: Int, val text: String) : ToDoItem()
}
