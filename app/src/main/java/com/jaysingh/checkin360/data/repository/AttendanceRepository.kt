package com.jaysingh.checkin360.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysingh.checkin360.data.model.AttendanceRecord
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class AttendanceRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun getTodayRecord(): AttendanceRecord? {
        val uid = auth.currentUser?.uid ?: return null
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()
        ).format(Date())

        return try {
            val snapshot = db.collection("attendance")
                .whereEqualTo("userId", uid)
                .whereEqualTo("date", today)
                .get().await()

            if (snapshot.documents.isNotEmpty()) {
                snapshot.documents[0]
                    .toObject(AttendanceRecord::class.java)
                    ?.copy(id = snapshot.documents[0].id)
            } else null
        } catch (e: Exception) { null }
    }

    // Punch In
    suspend fun punchIn(lat: Double, lng: Double): Result<String> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("Login required"))
        val now = Date()
        val today = SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).format(now)
        val timeStr = SimpleDateFormat(
            "hh:mm a", Locale.getDefault()
        ).format(now)

        // Late check — After 9:30 AM
        val cal = Calendar.getInstance()
        val isLate = cal.get(Calendar.HOUR_OF_DAY) > 9 ||
                (cal.get(Calendar.HOUR_OF_DAY) == 9 &&
                        cal.get(Calendar.MINUTE) >= 30)

        val record = AttendanceRecord(
            userId = uid,
            date = today,
            punchInTime = timeStr,
            punchInLat = lat,
            punchInLng = lng,
            status = if (isLate) "Late" else "Present"
        )

        return try {
            val ref = db.collection("attendance").add(record).await()
            Result.success(ref.id)
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    // Punch OUT
    suspend fun punchOut(
        recordId: String,
        lat: Double,
        lng: Double
    ): Result<Unit> {
        val timeStr = SimpleDateFormat(
            "hh:mm a", Locale.getDefault()
        ).format(Date())

        return try {
            db.collection("attendance").document(recordId)
                .update(mapOf(
                    "punchOutTime" to timeStr,
                    "punchOutLat" to lat,
                    "punchOutLng" to lng
                )).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Last 7 days records
    suspend fun getRecentRecords(): List<AttendanceRecord> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = db.collection("attendance")
                .whereEqualTo("userId", uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(7)
                .get().await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(AttendanceRecord::class.java)
                    ?.copy(id = doc.id)
            }
        } catch (e: Exception) { emptyList() }
    }

    // Monthly stats
    suspend fun getMonthlyStats(): Triple<Int, Int, Int> {
        val uid = auth.currentUser?.uid
            ?: return Triple(0, 0, 0)
        val month = SimpleDateFormat(
            "yyyy-MM", Locale.getDefault()
        ).format(Date())

        return try {
            val snapshot = db.collection("attendance")
                .whereEqualTo("userId", uid)
                .get().await()

            val records = snapshot.documents.mapNotNull {
                it.toObject(AttendanceRecord::class.java)
            }.filter { it.date.startsWith(month) }

            val present = records.count { it.status == "Present" }
            val late = records.count { it.status == "Late" }
            val leave = records.count { it.status == "Leave" }
            Triple(present, late, leave)
        } catch (e: Exception) {
            Triple(0, 0, 0)
        }
    }

    fun getCurrentUserName(): String =
        auth.currentUser?.email?.substringBefore("@")
            ?.replaceFirstChar { it.uppercase() } ?: "Employee"


}