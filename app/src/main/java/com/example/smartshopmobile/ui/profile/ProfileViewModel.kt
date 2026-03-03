package com.example.smartshopmobile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.api.UserService
import com.example.smartshopmobile.data.model.UserData
import com.example.smartshopmobile.data.repository.GenericRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val userService: UserService
) : ViewModel() {

    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            genericRepository.request { userService.getCurrentUser() }.collect { result ->
                result.onSuccess { response ->
                    _user.value = response.value?.data
                }.onFailure { e ->
                    _error.value = e.message
                }
                _isLoading.value = false
            }
        }
    }
}