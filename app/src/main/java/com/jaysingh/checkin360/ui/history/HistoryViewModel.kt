package com.jaysingh.checkin360.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysingh.checkin360.data.model.AttendanceRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _records =
        MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val records: StateFlow<List<AttendanceRecord>> = _records

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _stats = MutableStateFlow(Triple(0, 0, 0))
    val stats: StateFlow<Triple<Int, Int, Int>> = _stats

    private val _selectedMonth = MutableStateFlow("")
    val selectedMonth: StateFlow<String> = _selectedMonth

    init {
        val monthStr = SimpleDateFormat(
            "MMMM yyyy", Locale.getDefault()
        ).format(Date())
        _selectedMonth.value = monthStr
        loadRecords()
    }

    private fun loadRecords() {
        val uid = auth.currentUser?.uid ?: return
        val currentMonth = SimpleDateFormat(
            "yyyy-MM", Locale.getDefault()
        ).format(Date())

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("attendance")
                    .whereEqualTo("userId", uid)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get().await()

                val allRecords = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AttendanceRecord::class.java)
                        ?.copy(id = doc.id)
                }.filter { it.date.startsWith(currentMonth) }

                _records.value = allRecords

                // Stats calculate
                val present = allRecords.count{
                    it.status == "Present"
                }
                val late = allRecords.count { it.status == "Late" }
                val leave = allRecords.count { it.status == "Leave" }
                _stats.value = Triple(present, late, leave)

            } catch (e: Exception) {
                _records.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }

    }

}