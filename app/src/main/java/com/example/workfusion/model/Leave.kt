package com.example.workfusion.model

class Leave (
    val leaveId: Long = 0L,
    val empId: Long = 0L,
    val sn: Long=0L,

    val name: String = "",
    val type: String="",
    val subject: String="",
    val orgId:String = "",
    val reason: String = "",
    var status: String = "Not Started",
    val startDate: String = "",
    val endDate: String = ""

)