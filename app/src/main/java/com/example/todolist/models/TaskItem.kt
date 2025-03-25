package com.example.todolist.models

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val isImportant: Boolean = false
)