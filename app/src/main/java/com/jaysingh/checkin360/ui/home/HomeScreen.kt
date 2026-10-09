package com.jaysingh.checkin360.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jaysingh.checkin360.data.model.AttendanceRecord
import com.jaysingh.checkin360.ui.auth.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen (
    onLogout: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val punchState by viewModel.punchState.collectAsState()
    val recentRecords by viewModel.recentRecords.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val userName by viewModel.userName.collectAsState()

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[
            Manifest.permission.ACCESS_FINE_LOCATION
        ] == true
        if (granted) viewModel.handlePunch(context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // ------Header------
        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PrimaryBlue)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val hour = Calendar.getInstance()
                                .get(Calendar.HOUR_OF_DAY)
                            val greeting = when {
                                hour < 12 -> "Good morning"
                                hour < 17 -> "Good afternoon"
                                else -> "Good evening"
                            }
                            Text(
                                "$greeting, ",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp
                            )
                            Text(
                                userName,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Profile + Logout
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    userName.firstOrNull()
                                        ?.uppercaseChar()
                                        ?.toString() ?: "J",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = onLogout) {
                                Icon(
                                    Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    val dateStr = SimpleDateFormat(
                        "EEE, dd MMM yyyy", Locale.getDefault()
                    ).format(Date())
                    Text(
                        dateStr,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // ── Punch Card ──
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (val state = punchState) {
                        is PunchState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue)
                            }
                        }
                        is PunchState.NotPunchedIn -> {
                            StatusBadge("Not Checked In", Color(0xFFEF4444))
                            Spacer(Modifier.height(12.dp))
                            PunchTimesRow("--:--", "--:--")
                            Spacer(Modifier.height(16.dp))
                            PunchButton(
                                text = "Punch In",
                                isPunchIn = true,
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            )
                        }

                        is PunchState.PunchedIn -> {
                            StatusBadge("Working", Color(0xFF10B981))
                            Spacer(Modifier.height(12.dp))
                            PunchTimesRow(state.time, "--:--")
                            Spacer(Modifier.height(16.dp))
                            PunchButton(
                                text = "Punch Out",
                                isPunchIn = false,
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            )
                        }

                        is PunchState.PunchedOut -> {
                            StatusBadge("Day Complete ✓", Color(0xFF2563EB))
                            Spacer(Modifier.height(12.dp))
                            PunchTimesRow(state.inTime, state.outTime)
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {},
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = Color(0xFFE5E7EB)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    "Work Day Complete",
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        is PunchState.Error -> {
                            Text(
                                state.msg,
                                color = Color(0xFFEF4444),
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = { viewModel.loadData() }
                            ) {
                                Text("Retry", color = PrimaryBlue)
                            }
                        }
                    }
                }
            }
        }

        // ── Stats Row ──
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard("Present", stats.first.toString(),
                    Color(0xFFEAF3DE), Modifier.weight(1f))
                StatCard("Late", stats.second.toString(),
                    Color(0xFFFAEEDA), Modifier.weight(1f))
                StatCard("Leave", stats.third.toString(),
                    Color(0xFFE6F1FB), Modifier.weight(1f))
            }
        }

        // ── Recent Records ──
        item {
            Text(
                "Recent Records",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(
                    start = 16.dp, top = 20.dp, bottom = 10.dp
                )
            )
        }

        if (recentRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No records found",
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(recentRecords) { record ->
                AttendanceItem(record)
            }
        }

        item {Spacer(Modifier.height(80.dp)) }

    }
}

// ── Reusable Components ──

@Composable
fun StatusBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.12f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PunchTimesRow(inTime: String, outTime: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("PUNCH IN", fontSize = 10.sp,
                color = Color(0xFF9CA3AF))
            Text(inTime, fontSize = 22.sp,
                fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("PUNCH OUT", fontSize = 10.sp,
                color = Color(0xFF9CA3AF))
            Text(outTime, fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (outTime == "--:--")
                    Color(0xFF9CA3AF) else Color(0xFF10B981))
        }
    }
}


@Composable
fun PunchButton(
    text: String,
    isPunchIn: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPunchIn) PrimaryBlue
            else Color(0xFFEAF3DE)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text,
            color = if (isPunchIn) Color.White else Color(0xFF27500A),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatCard(label: String,
             value: String,
             bgColor: Color,
             modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 11.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
fun AttendanceItem(record: AttendanceRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(record.date, fontSize = 13.sp,
                    fontWeight = FontWeight.Medium)
                Text(
                    buildString {
                        append("In: ${record.punchInTime}")
                        if (record.punchOutTime.isNotEmpty())
                            append(" · Out: ${record.punchOutTime}")
                    },
                    fontSize = 11.sp, color = Color(0xFF6B7280)
                )
            }
            val (badgeColor, textColor) = when (record.status) {
                "Present" -> Color(0xFFEAF3DE) to Color(0xFF27500A)
                "Late" -> Color(0xFFFAEEDA) to Color(0xFF633806)
                "Absent" -> Color(0xFFFCEBEB) to Color(0xFF791F1F)
                else -> Color(0xFFE6F1FB) to Color(0xFF0C447C)
            }
            Box(
                modifier = Modifier
                    .background(badgeColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(record.status, fontSize = 11.sp,
                    color = textColor, fontWeight = FontWeight.Medium)
            }
        }
    }
}












