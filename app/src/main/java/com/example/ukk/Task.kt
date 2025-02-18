package com.example.ukk

data class Task(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val category: String,
    val status: Int
)
