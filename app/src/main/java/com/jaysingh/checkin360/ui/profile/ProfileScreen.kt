package com.jaysingh.checkin360.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jaysingh.checkin360.ui.auth.PrimaryBlue


@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogOut()
                }) {
                    Text("Confirm Logout", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column {
        // ── Blue Header with Avatar ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(bottom = 16.dp, top = 30.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Profile",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        profile.initials,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    profile.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    profile.role,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // ── Info Card ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        ProfileInfoRow(
                            icon = Icons.Default.Badge,
                            label = "Employee ID",
                            value = profile.employeeId
                        )
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        ProfileInfoRow(
                            icon = Icons.Default.Business,
                            label = "Department",
                            value = profile.department
                        )
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email Address",
                            value = profile.email
                        )
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Phone Number",
                            value = profile.phone
                        )
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        ProfileInfoRow(
                            icon = Icons.Default.CalendarMonth,
                            label = "Date of Joining",
                            value = profile.joiningDate
                        )
                    }
                }
            }

            // ── Settings Card ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        ProfileActionRow(
                            icon = Icons.Default.Notifications,
                            label = "Notification Settings",
                            iconTint = PrimaryBlue,
                            onClick = { }
                        )
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        ProfileActionRow(
                            icon = Icons.Default.Lock,
                            label = "Change Password",
                            iconTint = PrimaryBlue,
                            onClick = { }
                        )
                        HorizontalDivider(color = Color(0xFFF3F4F6))
                        ProfileActionRow(
                            icon = Icons.Default.Logout,
                            label = "Log out",
                            labelColor = Color(0xFFEF4444),
                            iconTint = Color(0xFFEF4444),
                            onClick = { showLogoutDialog = true }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}



@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )
            Text(
                value.ifEmpty { "Not set" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF111827)
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFD1D5DB),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun ProfileActionRow(
    icon: ImageVector,
    label: String,
    labelColor: Color = Color(0xFF111827),
    iconTint: Color = PrimaryBlue,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            label,
            fontSize = 14.sp,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFD1D5DB),
            modifier = Modifier.size(18.dp)
        )
    }
}