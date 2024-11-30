package com.example.workfusion.model

class Leave (
    val leaveId: Long = 0L,          // Default value
    val empId: Long = 0L,
    val sn: Long=0L,
    // Default value
    val name: String = "",
    val type: String="",
    val subject: String="",
    val orgId:String = "",
    val reason: String = "",   // Default value
    val status: String = "Not Started", // Default value
    val startDate: String = "",     // Default value
    val endDate: String = ""        // Default value

)