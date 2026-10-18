package com.jaysingh.checkin360.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysingh.checkin360.data.model.LeaveRequest
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LeaveRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // Leave Submission
    suspend fun applyLeave(
        leaveType: String,
        fromDate: String,
        toDate: String,
        reason: String,
    ) : Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Login required"))
        val userName = auth.currentUser?.email
            ?.substringBefore("@") ?: "Employee"

        // Total days calculation
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val days = try {
            val from = sdf.parse(fromDate)!!
            val to = sdf.parse(toDate)!!
            val diff = to.time - from.time
            (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) +1).toInt()
        } catch (e: Exception) {1}

        val appliedOn = SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).format(Date())

        val request = LeaveRequest(
            userId = uid,
            userName = userName,
            leaveType = leaveType,
            fromDate = fromDate,
            toDate = toDate,
            reason = reason,
            totalDays = days,
            status = "Pending",
            appliedOn = appliedOn
        )

        return try {
            db.collection("leaveRequests").add(request).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Leave request fetch
    suspend fun getMyLeaves(): List<LeaveRequest> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = db.collection("leaveRequests")
                .whereEqualTo("userId", uid)
                .get().await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(LeaveRequest::class.java)
                    ?.copy(id = doc.id)
            }.sortedByDescending { it.appliedOn }
        } catch (e: Exception) { emptyList() }
    }

    // Leave balance
    suspend fun getLeaveBalance(): Int{
        val uid = auth.currentUser?.uid ?: return 18
        return try {
            val snapshot = db.collection("leaveRequests")
                .whereEqualTo("userId", uid)
                .whereEqualTo("status", "Approved")
                .get().await()

            val usedDays = snapshot.documents.mapNotNull {
                it.toObject(LeaveRequest::class.java)
            }.sumOf { it.totalDays }

            maxOf(0, 18 - usedDays)  // Max leave balance is 18 e.g.= (18 = annual quota leave)
        } catch (e: Exception) { 18 }
    }
}