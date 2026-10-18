package com.jaysingh.checkin360.ui.leave

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jaysingh.checkin360.data.model.LeaveRequest
import com.jaysingh.checkin360.ui.auth.PrimaryBlue

@Composable
fun LeaveScreen(
    onBack: () -> Unit,
    viewModel: LeaveViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val myLeaves by viewModel.myLeaves.collectAsState()

    // Success snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState) {
        if (uiState is LeaveUiState.Success) {
            snackbarHostState.showSnackbar("Leave request submitted!")
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)}
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
        ) {
            // ── Header ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryBlue)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Apply Leave",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ── Balance Banner ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE6F1FB))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Remaining Annual Leave balance: $balance Days",
                    fontSize = 13.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            // ── Tabs ──
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryBlue
            ) {
                listOf("Apply", "My Leaves").forEachIndexed { i, title ->
                    Tab(
                        selected = selectedTab == i,
                        onClick = {viewModel.onTabSelect(i)},
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == i)
                                    FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // ── Tab Content ──
            when (selectedTab) {
                0 -> ApplyLeaveTab(viewModel, uiState)
                1 -> MyLeavesTab(myLeaves)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLeaveTab(
    viewModel: LeaveViewModel,
    uiState: LeaveUiState
) {
    val selectedType by viewModel.selectedType.collectAsState()
    val fromDate by viewModel.fromDate.collectAsState()
    val toDate by viewModel.toDate.collectAsState()
    val reason by viewModel.reason.collectAsState()
    val totalDays by viewModel.totalDays.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Leave Type
        item {
            Text(
                "Leave Type",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            // Dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color(0xFFE5E7EB)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.leaveTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                viewModel.onTypeSelect(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // From Date
        item {
            Text(
                "From Date",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = fromDate,
                onValueChange = viewModel::onFromDateChange,
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("05 Jun 2025") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )
        }

        // To Date
        item {
            Text(
                "To Date",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = toDate,
                onValueChange = viewModel::onDateChange,
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("06 Jun 2025") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )
        }

        // Reason
        item {
            Text(
                "Reason",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = reason,
                onValueChange = viewModel::onReasonChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(10.dp),
                placeholder = {
                    Text("Describe your reason...")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )
        }

        // Total Days
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFEAF3DE),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Total Days",
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )
                Text(
                    "$totalDays ${if (totalDays > 1) "Days" else "Day"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF27500A)
                )
            }
        }

        // Error
        if (uiState is LeaveUiState.Error) {
            item {
                Text(
                    (uiState as LeaveUiState.Error).message,
                    color = Color(0xFFEF4444),
                    fontSize = 13.sp
                )
            }
        }

        // Submit Button
        item {
            Button(
                onClick = { viewModel.submitLeave() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = uiState !is LeaveUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (uiState is LeaveUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Submit Request",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
fun MyLeavesTab(leaves: List<LeaveRequest>) {
    if (leaves.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No leave requests found",
                color = Color(0xFF9CA3AF),
                fontSize = 15.sp
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(leaves) { leave ->
            LeaveItem(leave)
        }
    }
}

@Composable
fun LeaveItem(leave: LeaveRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    leave.leaveType,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                // Status badge
                val (bg, tc) = when (leave.status) {
                    "Approved" ->
                        Color(0xFFEAF3DE) to Color(0xFF27500A)
                    "Rejected" ->
                        Color(0xFFFCEBEB) to Color(0xFF791F1F)
                    else ->
                        Color(0xFFFAEEDA) to Color(0xFF633806)
                }
                Box(
                    modifier = Modifier
                        .background(bg, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        leave.status,
                        fontSize = 11.sp,
                        color = tc,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "${leave.fromDate} → ${leave.toDate}",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                "${leave.totalDays} day(s) · Applied: ${leave.appliedOn}",
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )
            if (leave.reason.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    leave.reason,
                    fontSize = 12.sp,
                    color = Color(0xFF374151)
                )
            }
        }
    }
}













