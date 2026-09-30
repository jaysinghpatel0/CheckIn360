package com.jaysingh.checkin360.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel : ViewModel() {

    private val auth = Firebase.auth

    init {
        auth.signOut()
    }

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    fun onEmailChange(value: String) {_email.value = value}
    fun onPasswordChange(value: String) {_password.value = value}
    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun checkCurrentUser(): Boolean = auth.currentUser != null

    fun login() {
        val email = _email.value.trim()
        val password = _password.value.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = LoginUiState.Error("Email and password cannot be empty")
            return
        }
        if (password.length < 6) {
            _uiState.value = LoginUiState.Error("Password must be at least 6 characters long")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = LoginUiState.Error("Invalid email address")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()

                Log.d("CheckIn360_Auth", "Login Successful! User UID: ${auth.currentUser?.uid}")

                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(
                    when {
                        e.message?.contains("credential", ignoreCase = true) == true ||
                                e.message?.contains("password", ignoreCase = true) == true ->
                            "Invalid password"

                        e.message?.contains("no user", ignoreCase = true) == true ->
                            "No user found with this email"

                        e.message?.contains("network", ignoreCase = true) == true ->
                            "Please check your internet connection"

                        else -> "Oops! Something is wrong. Please try again later."
                    }
                )
            }
        }
    }
    fun resetState() { _uiState.value = LoginUiState.Idle }
}