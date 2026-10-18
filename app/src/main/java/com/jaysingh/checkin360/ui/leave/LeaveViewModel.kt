package com.jaysingh.checkin360.ui.leave

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaysingh.checkin360.data.model.LeaveRequest
import com.jaysingh.checkin360.data.repository.LeaveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


sealed class LeaveUiState {
    object Idle : LeaveUiState()
    object Loading : LeaveUiState()
    object Success : LeaveUiState()
    data class Error(val message: String) : LeaveUiState()
}

class LeaveViewModel : ViewModel() {

    private val repository = LeaveRepository()

    // Form fields
    private val _selectedType = MutableStateFlow("Annual Leave")
    val selectedType: StateFlow<String> = _selectedType

    private val _fromDate = MutableStateFlow("")
    val fromDate: StateFlow<String> = _fromDate

    private val _toDate = MutableStateFlow("")
    val toDate: StateFlow<String> = _toDate

    private val _reason = MutableStateFlow("")
    val reason: StateFlow<String> = _reason

    private val _totalDays = MutableStateFlow(0)
    val totalDays: StateFlow<Int> = _totalDays

    // UI State
    private val _uiState = MutableStateFlow<LeaveUiState>(LeaveUiState.Idle)
    val uiState: StateFlow<LeaveUiState> = _uiState

    // Leave balance + history
    private val _balance = MutableStateFlow(18)
    val balance: StateFlow<Int> = _balance

    private val _myLeaves = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val myLeaves: StateFlow<List<LeaveRequest>> = _myLeaves


    // Tabs
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    val leaveTypes = listOf(
        "Annual Leave", "Sick Leave","Casual Leave",
//        "Maternity Leave",
//        "Paternal Leave"
    )

    init {
        loadData()
        // Default dates for today
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val today = Calendar.getInstance()
        _fromDate.value = sdf.format(today.time)
        today.add(Calendar.DAY_OF_MONTH, 1)
        _toDate.value = sdf.format(today.time)
        calculateDays()
    }

    fun onTypeSelect(type: String) { _selectedType.value = type }
    fun onReasonChange(value: String) { _reason.value = value }
    fun onTabSelect(index: Int) { _selectedTab.value = index }

    fun onFromDateChange(date: String) {
        _fromDate.value = date
        calculateDays()
    }

    fun onDateChange(date: String) {
        _toDate.value = date
        calculateDays()
    }

    private fun calculateDays() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        try {
            val from = sdf.parse(_fromDate.value)!!
            val to = sdf.parse(_toDate.value)!!
            val diff = to.time - from.time
            val days = (diff / (24 * 60 * 60 * 1000) + 1).toInt()
            _totalDays.value = maxOf(1, days)
        } catch (e: Exception) {
            _totalDays.value = 1
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _balance.value = repository.getLeaveBalance()
            _myLeaves.value = repository.getMyLeaves()
        }
    }

    fun submitLeave() {
        if (_reason.value.trim().isEmpty()) {
            _uiState.value = LeaveUiState.Error("Reason cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = LeaveUiState.Loading
            repository.applyLeave(
                leaveType = _selectedType.value,
                fromDate = _fromDate.value,
                toDate = _toDate.value,
                reason = _reason.value.trim()
            ).onSuccess {
                _uiState.value = LeaveUiState.Success
                //Form Reset
                _reason.value = ""
                loadData()
            }.onFailure {
                _uiState.value = LeaveUiState.Error(
                    it.message ?: "Submit failed"
                )
            }
        }
    }

    fun resetState() { _uiState.value = LeaveUiState.Idle }
}













