package com.example.smartshopmobile.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.model.LoginRequest
import com.example.smartshopmobile.data.model.RegisterRequest
import com.example.smartshopmobile.data.model.UserData
import com.example.smartshopmobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val TAG = "SmartShop_AuthVM"

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()

    init {
        Log.d(TAG, "Initializing AuthViewModel")
        checkAuth()
    }

    private fun checkAuth() {
        if (repository.hasToken()) {
            Log.d(TAG, "Token found, fetching current user")
            fetchCurrentUser()
        } else {
            Log.d(TAG, "No token found")
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "Login attempt for: $email")
            _loginState.value = AuthState.Loading
            repository.login(LoginRequest(email, password)).collect { result ->
                result.fold(
                    onSuccess = { 
                        Log.d(TAG, "Login successful, fetching profile")
                        fetchCurrentUser(isLogin = true)
                    },
                    onFailure = { 
                        Log.e(TAG, "Login failed: ${it.message}")
                        _loginState.value = AuthState.Error(it.message ?: "Unknown error") 
                    }
                )
            }
        }
    }

    private fun fetchCurrentUser(isLogin: Boolean = false) {
        viewModelScope.launch {
            Log.d(TAG, "Fetching current user profile (isLogin=$isLogin)")
            repository.getCurrentUser().collect { result ->
                result.fold(
                    onSuccess = { response ->
                        val user = response.value?.data
                        Log.d(TAG, "User profile fetched: ${user?.username}, Role: ${user?.role}")
                        _currentUser.value = user
                        if (isLogin) {
                            _loginState.value = AuthState.Success(user)
                        }
                    },
                    onFailure = { 
                        Log.e(TAG, "Failed to fetch current user: ${it.message}")
                        if (isLogin) {
                            _loginState.value = AuthState.Error("Failed to fetch user profile")
                        }
                    }
                )
            }
        }
    }

    fun register(username: String, email: String, pass: String, gender: Boolean) {
        viewModelScope.launch {
            Log.d(TAG, "Registering user: $username")
            _registerState.value = AuthState.Loading
            repository.register(RegisterRequest(email, pass, username, gender)).collect { result ->
                result.fold(
                    onSuccess = { 
                        Log.d(TAG, "Registration successful")
                        _registerState.value = AuthState.Success() 
                    },
                    onFailure = { 
                        Log.e(TAG, "Registration failed: ${it.message}")
                        _registerState.value = AuthState.Error(it.message ?: "Unknown error") 
                    }
                )
            }
        }
    }

    fun logout() {
        Log.d(TAG, "Logging out")
        repository.logout()
        _currentUser.value = null
        _loginState.value = AuthState.Idle
    }

    fun resetState() {
        Log.d(TAG, "Resetting auth states")
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}
