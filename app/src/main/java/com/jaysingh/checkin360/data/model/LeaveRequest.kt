package com.jaysingh.checkin360.data.model

data class LeaveRequest(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val leaveType: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val reason: String = "",
    val totalDays: Int = 0,
    val status: String = "Pending",   // Pending/Approved/Rejected
    val appliedOn: String = ""
)
