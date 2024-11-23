package com.example.workfusion.model

class Leave (
    val leaveId: Long = 0L,          // Default value
    val empId: Long = 0L,           // Default value
    val name: String = "",          // Default value
    val reason: String = "",   // Default value
    val status: String = "Not Started", // Default value
    val startDate: String = "",     // Default value
    val endDate: String = ""        // Default value
)