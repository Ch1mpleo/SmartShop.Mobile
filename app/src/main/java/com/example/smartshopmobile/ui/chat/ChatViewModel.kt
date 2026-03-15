package com.example.smartshopmobile.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshopmobile.data.model.ChatMessageDto
import com.example.smartshopmobile.data.network.ChatManager
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatManager: ChatManager
) : ViewModel() {

    val messages: StateFlow<List<ChatMessageDto>> = chatManager.messages
    val connectionState: StateFlow<HubConnectionState> = chatManager.connectionState

    init {
        chatManager.initConnection()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            chatManager.sendMessage(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatManager.stopConnection()
    }
}
