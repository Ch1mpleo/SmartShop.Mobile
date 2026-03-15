package com.example.smartshopmobile.data.network

import android.util.Log
import com.example.smartshopmobile.data.local.TokenManager
import com.example.smartshopmobile.data.model.ChatMessageDto
import com.example.smartshopmobile.data.model.SendChatMessageDto
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private var hubConnection: HubConnection? = null
    private val TAG = "SmartShop_ChatManager"
    private val BASE_URL = "http://10.0.2.2:5000/chatHub"
    private val disposables = CompositeDisposable()

    private val _messages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val messages: StateFlow<List<ChatMessageDto>> = _messages.asStateFlow()

    private val _connectionState = MutableStateFlow(HubConnectionState.DISCONNECTED)
    val connectionState: StateFlow<HubConnectionState> = _connectionState.asStateFlow()

    fun initConnection() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            return
        }

        val token = tokenManager.getToken()
        if (token == null) {
            Log.e(TAG, "Cannot init connection: No token found")
            return
        }

        hubConnection = HubConnectionBuilder.create(BASE_URL)
            .withAccessTokenProvider(Single.just(token))
            .build()

        hubConnection?.on("ReceiveMessage", { message ->
            Log.d(TAG, "New message received: ${message.message}")
            val currentList = _messages.value.toMutableList()
            currentList.add(message)
            _messages.value = currentList
        }, ChatMessageDto::class.java)

        hubConnection?.on("ReceiveChatHistory", { history ->
            Log.d(TAG, "Chat history received: ${history.size} messages")
            _messages.value = history.toList()
        }, Array<ChatMessageDto>::class.java)

        hubConnection?.on("MessageDeleted", { messageId ->
            Log.d(TAG, "Message deleted: $messageId")
            _messages.value = _messages.value.filter { it.id != messageId.toString() }
        }, String::class.java)

        hubConnection?.onClosed {
            Log.d(TAG, "Connection closed")
            _connectionState.value = HubConnectionState.DISCONNECTED
        }

        startConnection()
    }

    private fun startConnection() {
        _connectionState.value = HubConnectionState.CONNECTING
        val disposable = hubConnection?.start()?.subscribe({
            Log.d(TAG, "Connection started successfully")
            _connectionState.value = HubConnectionState.CONNECTED
            getChatHistory()
        }, { error ->
            Log.e(TAG, "Error starting connection: ${error.message}")
            _connectionState.value = HubConnectionState.DISCONNECTED
        })
        disposable?.let { disposables.add(it) }
    }

    fun sendMessage(message: String) {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            hubConnection?.send("SendMessage", SendChatMessageDto(message))
        } else {
            Log.e(TAG, "Cannot send message: Not connected. Current state: ${hubConnection?.connectionState}")
        }
    }

    fun getChatHistory() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            hubConnection?.send("GetChatHistory", 1, 50)
        }
    }

    fun stopConnection() {
        val disposable = hubConnection?.stop()?.subscribe({
            Log.d(TAG, "Connection stopped")
            _connectionState.value = HubConnectionState.DISCONNECTED
            hubConnection = null
            _messages.value = emptyList()
        }, { error ->
            Log.e(TAG, "Error stopping connection: ${error.message}")
        })
        disposable?.let { disposables.add(it) }
    }
}
