package com.example.ukk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val categoryId: Int
)
