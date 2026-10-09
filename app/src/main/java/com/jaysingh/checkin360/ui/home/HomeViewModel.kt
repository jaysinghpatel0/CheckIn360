package com.jaysingh.checkin360.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.jaysingh.checkin360.data.model.AttendanceRecord
import com.jaysingh.checkin360.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Punch states
sealed class PunchState {
    object Loading : PunchState()
    object NotPunchedIn : PunchState()
    data class PunchedIn(val time: String) : PunchState()
    data class PunchedOut(
        val inTime: String,
        val outTime: String
    ) : PunchState()
    data class Error(val msg: String) : PunchState()
}


class HomeViewModel : ViewModel() {

    private val repository = AttendanceRepository()

    private val _punchState = MutableStateFlow<PunchState>(
        PunchState.Loading
    )
    val punchState: StateFlow<PunchState> = _punchState

    private val _recentRecords =
        MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val recentRecords: StateFlow<List<AttendanceRecord>> =
        _recentRecords

    private val _stats = MutableStateFlow(Triple(0, 0, 0))
    val stats: StateFlow<Triple<Int, Int, Int>> = _stats

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private var todayRecordId: String? = null

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _userName.value = repository.getCurrentUserName()
            checkTodayStatus()
            loadRecentRecords()
            loadStats()
        }
    }

    private suspend fun checkTodayStatus() {
        _punchState.value = PunchState.Loading
        val record = repository.getTodayRecord()
        todayRecordId = record?.id

        _punchState.value = when {
            record == null -> PunchState.NotPunchedIn
            record.punchOutTime.isEmpty() ->
                PunchState.PunchedIn(record.punchInTime)
            else -> PunchState.PunchedOut(
                record.punchInTime, record.punchOutTime
            )
        }
    }

    private suspend fun loadRecentRecords() {
        _recentRecords.value = repository.getRecentRecords()
    }

    private suspend fun loadStats() {
        _stats.value = repository.getMonthlyStats()
    }

    // Get Location through the GPS and punch it
    fun handlePunch(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _punchState.value = PunchState.Error(
                "Location permission is required"
            )
            return
        }

        _punchState.value = PunchState.Loading

        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L
        ).setMaxUpdates(1).build()

        client.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation
                    if (loc != null) {
                        doPunch(loc.latitude, loc.longitude)
                    } else {
                        _punchState.value =
                            PunchState.Error("Location not found")
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun doPunch(lat: Double, lng: Double) {
        viewModelScope.launch {
            if (todayRecordId == null) {
                // Punch In
                repository.punchIn(lat, lng)
                    .onSuccess { id ->
                        todayRecordId = id
                        checkTodayStatus()
                        loadRecentRecords()
                        loadStats()
                    }
                    .onFailure {
                        _punchState.value =
                            PunchState.Error(it.message ?: "Error")
                    }
            } else {
                // Punch Out
                repository.punchOut(todayRecordId!!, lat, lng)
                    .onSuccess {
                        checkTodayStatus()
                        loadRecentRecords()
                    }
                    .onFailure {
                        _punchState.value =
                            PunchState.Error(it.message ?: "Error")
                    }
            }
        }
    }

}










