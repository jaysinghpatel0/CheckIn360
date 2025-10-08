package com.example.checkin360

data class Employee(val id: Int, val name: String, val department: String, val designation: String)

fun greetEmployee(employee: Employee): String{
    return "${employee.name} ${employee.designation} ${employee.department}"
}
