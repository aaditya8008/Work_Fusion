package com.example.workfusion.model



data class Task(
    val taskId: Long = 0L,          // Default value
    val empId: Long = 0L,           // Default value
    val name: String = "",          // Default value
    val description: String = "",   // Default value
    val status: String = "Not Started", // Default value
    val startDate: String = "",     // Default value
    val endDate: String = ""        // Default value
)
