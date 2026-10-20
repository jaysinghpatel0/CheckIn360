package com.jaysingh.checkin360.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


data class ProfileData(
    val name: String = "",
    val email: String = "",
    val employeeId: String = "",
    val department: String = "",
    val phone: String = "",
    val joiningDate: String = "",
    val role: String = "Android Developer",
    val initials: String = ""
)

class ProfileViewModel: ViewModel() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _profile = MutableStateFlow(ProfileData())
    val profile: StateFlow<ProfileData> = _profile

    init { loadProfile() }

    private fun loadProfile() {
        val user = auth.currentUser ?: return
        val email = user.email ?: ""
        val nameFromEmail = email.substringBefore("@")
            .replace(".", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
        val initials = nameFromEmail
            .split(" ")
            .take(2)
            .joinToString("") { it.firstOrNull()?.toString() ?: "" }
            .uppercase()

        //first set to basic data from email
        _profile.value = ProfileData(
            name = nameFromEmail,
            email = email,
            employeeId = "CE-2024-${user.uid.take(3).uppercase()}",
            department = "Engineering",
            phone = "+91 XXXXXXXXXX",
            joiningDate = "01 Jan 2024",
            role = "Android Developer",
            initials = initials.ifEmpty { "JS" }
        )

        // Store load data from firestore (If available)
        viewModelScope.launch {
            try {
                val doc = db.collection("users")
                    .document(user.uid)
                    .get().await()

                if (doc.exists()) {
                    _profile.value = _profile.value.copy(
                        employeeId = doc.getString("employeeId")
                            ?: _profile.value.employeeId,
                        department = doc.getString("department")
                            ?: _profile.value.department,
                        phone = doc.getString("phone")
                            ?: _profile.value.phone,
                        joiningDate = doc.getString("joiningDate")
                            ?: _profile.value.joiningDate,
                        role = doc.getString("role")
                            ?: _profile.value.role
                    )
                }
            } catch (e: Exception) {
                // The data from the email is already set
            }
        }
    }

}