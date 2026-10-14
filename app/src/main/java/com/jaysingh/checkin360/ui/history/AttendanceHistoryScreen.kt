package com.jaysingh.checkin360.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jaysingh.checkin360.data.model.AttendanceRecord
import com.jaysingh.checkin360.ui.auth.PrimaryBlue


@Composable
fun AttendanceHistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val records by viewModel.records.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // ── Header ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "Attendance History",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        selectedMonth,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Stats chips ──
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatChip(
                        "${stats.first} Present",
                        Color(0xFF27500A),
                        Color(0xFFEAF3DE)
                    )
                    StatChip(
                        "${stats.second} Late",
                        Color(0xFF633806),
                        Color(0xFFFAEEDA)
                    )
                    StatChip(
                        "${stats.third} Leave",
                        Color(0xFF0C447C),
                        Color(0xFFE6F1FB)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Loading ──
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            }

            // ── Records list ──
            if (!isLoading && records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No records found",
                            color = Color(0xFF9CA3AF),
                            fontSize = 15.sp
                        )
                    }
                }
            }

            items (records) {records ->
                HistoryItem(records)
            }
        }
    }

}


@Composable
fun StatChip(text: String, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun HistoryItem(record: AttendanceRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left — Date + times
            Column {
                // Date + day
                val dayName = try {
                    val sdf = java.text.SimpleDateFormat(
                        "yyyy-MM-dd", java.util.Locale.getDefault()
                    )
                    val date = sdf.parse(record.date)
                    val daySdf = java.text.SimpleDateFormat(
                        "dd MMM — EEE" , java.util.Locale.getDefault()
                    )
                    daySdf.format(date!!)
                } catch (e: Exception) { record.date }

                Text(
                    dayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Spacer(Modifier.height(4.dp))

                // Punch times
                if (record.punchInTime.isNotEmpty()) {
                    Text(
                        buildString {
                            append("In ${record.punchInTime}")
                            if (record.punchOutTime.isNotEmpty())
                                append(" · Out ${record.punchOutTime}")
                            else append(" · Working")
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                } else {
                    Text(
                        when (record.status) {
                            "Leave" -> "Leave Approved"
                            "Weekend" -> "Weekend"
                            else -> "Not Checked In"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }

            // Right — Status badge
            val (bgColor, textColor) = when (record.status) {
                "Present" -> Color(0xFFEAF3DE) to Color(0xFF27500A)
                "Late" -> Color(0xFFFAEEDA) to Color(0xFF633806)
                "Absent" -> Color(0xFFFCEBEB) to Color(0xFF791F1F)
                "Leave" -> Color(0xFFE6F1FB) to Color(0xFF0C447C)
                "Weekend", "Off" ->
                    Color(0xFFF3F4F6) to Color(0xFF6B7280)
                else -> Color(0xFFF3F4F6) to Color(0xFF6B7280)
            }
            Box(
                modifier = Modifier
                    .background(bgColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    record.status,
                    fontSize = 12.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}






