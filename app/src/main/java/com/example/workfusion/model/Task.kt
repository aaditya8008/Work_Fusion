package com.example.workfusion.model



data class Task(
    val taskId: Long = 0L,
    val empId: Long = 0L,
    val name: String = "",
    val description: String = "",
    val status: String = "Not Started",
    val startDate: String = "",
    val endDate: String = ""
)
