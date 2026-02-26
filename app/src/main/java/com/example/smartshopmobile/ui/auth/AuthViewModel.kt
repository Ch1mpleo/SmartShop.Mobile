package com.example.smartshopmobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.model.LoginRequest
import com.example.smartshopmobile.data.model.RegisterRequest
import com.example.smartshopmobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            repository.login(LoginRequest(email, password)).collect { result ->
                result.fold(
                    onSuccess = { _loginState.value = AuthState.Success },
                    onFailure = { _loginState.value = AuthState.Error(it.message ?: "Unknown error") }
                )
            }
        }
    }

    fun register(username: String, email: String, pass: String, gender: Boolean) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            repository.register(RegisterRequest(email, pass, username, gender)).collect { result ->
                result.fold(
                    onSuccess = { _registerState.value = AuthState.Success },
                    onFailure = { _registerState.value = AuthState.Error(it.message ?: "Unknown error") }
                )
            }
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}