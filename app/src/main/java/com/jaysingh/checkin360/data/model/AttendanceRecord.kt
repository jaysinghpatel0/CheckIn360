package com.jaysingh.checkin360.data.model

data class AttendanceRecord (
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val punchInTime: String = "",
    val punchOutTime: String = "",
    val punchInLat: Double = 0.0,
    val punchInLng: Double = 0.0,
    val punchOutLat: Double = 0.0,
    val punchOutLng: Double = 0.0,
    val status: String = "Present",     // Present / Late / Absent
    val workingHors: String = ""
)