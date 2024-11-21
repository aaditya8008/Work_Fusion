package com.example.workfusion.model

data class Task(
    val taskId: String,
    val empId: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String
)