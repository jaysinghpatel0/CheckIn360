package com.jaysingh.checkin360.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val employeeId: String = "",
    val department: String = "",
    val role: String = "employee"
)
