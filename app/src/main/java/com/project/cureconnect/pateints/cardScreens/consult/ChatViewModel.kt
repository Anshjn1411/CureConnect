package com.project.cureconnect.pateints.cardScreens.consult

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import login.AuthViewModel

class ChatViewModel : ViewModel() {
    val viewModel1: AuthViewModel = AuthViewModel()

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _connectedEndpoint = MutableStateFlow<String?>(null)
    val connectedEndpoint = _connectedEndpoint.asStateFlow()

    // Connection status
    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    // Connection error state
    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError = _connectionError.asStateFlow()

    // User info states
    private val _remoteUserName = MutableStateFlow<String?>(null)
    val remoteUserName = _remoteUserName.asStateFlow()

    private val _remoteUserRole = MutableStateFlow<String?>(null)
    val remoteUserRole = _remoteUserRole.asStateFlow()

    private val _localUserName = MutableStateFlow<String?>(null)
    val localUserName = _localUserName.asStateFlow()

    private val _localUserRole = MutableStateFlow<String?>(null)
    val localUserRole = _localUserRole.asStateFlow()

    private lateinit var context: Context
    private val connectionRetryHandler = Handler(Looper.getMainLooper())
    private var connectionRetryCount = 0
    private val MAX_RETRY_COUNT = 3
    private val gson = Gson()

    // Service ID for the application
    private val SERVICE_ID = "com.project.cureconnect"

    data class UserInfo(
        val name: String,
        val role: String,
        val userId: String,
        val isInfoMessage: Boolean = true
    )

    fun setContext(context: Context) {
        this.context = context
        // Automatically start discovery and advertising when context is set
        loadUserInfo()
    }

    fun loadUserInfo() {
        viewModel1.getUserData(onResult = { userModel ->
            Log.d("user123", userModel.toString())
            _localUserName.value = userModel?.name ?: "User"
            _localUserRole.value = userModel?.role ?: "patient"
            Log.d("CureConnectUserData", "Loaded user info: ${_localUserName.value}, role: ${_localUserRole.value}")
            startConnection()
        })
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d("CureConnect", "Connection initiated with ${info.endpointName}")
            // Automatically accept connections
            Nearby.getConnectionsClient(context)
                .acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                _connectedEndpoint.value = endpointId
                _isConnecting.value = false
                connectionRetryCount = 0
                _messages.value = _messages.value + "System: Connection established"
                Log.d("CureConnect", "Connected to: $endpointId")

                // Send user info upon successful connection
                sendUserInfo()
            } else {
                Log.e("CureConnect", "Connection failed: ${result.status.statusMessage}")

                // Retry connection if we haven't reached the max retry count
                if (connectionRetryCount < MAX_RETRY_COUNT) {
                    connectionRetryCount++
                    connectionRetryHandler.postDelayed({
                        retryConnection()
                    }, 2000) // Wait 2 seconds before retrying
                } else {
                    _isConnecting.value = false
                    _connectionError.value = "Failed to connect after multiple attempts."
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            if (_connectedEndpoint.value == endpointId) {
                _connectedEndpoint.value = null
                _messages.value = _messages.value + "System: Connection lost. Reconnecting..."

                // Try to reconnect automatically
                retryConnection()

                Log.d("CureConnect", "Disconnected from: $endpointId")
            }
        }
    }

    private fun retryConnection() {
        Log.d("CureConnect", "Retrying connection (attempt $connectionRetryCount)")
        // Restart advertising and discovery
        startConnection()
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let {
                val message = String(it)
                Log.d("CureConnect", "📩 Message received from $endpointId: $message")

                // Try to parse as UserInfo
                try {
                    val userInfo = gson.fromJson(message, UserInfo::class.java)
                    if (userInfo.isInfoMessage) {
                        // This is a user info message
                        _remoteUserName.value = userInfo.name
                        _remoteUserRole.value = userInfo.role
                        Log.d("username", "${_remoteUserName.value}")

                        val roleText = if (userInfo.role == "doctor") "healthcare provider" else "patient"
                        _messages.value = _messages.value + "System: Connected to ${userInfo.name}"
                        return
                    }
                } catch (e: Exception) {
                    // Not a user info message, continue with regular message processing
                    Log.d("CureConnect", "Not a user info message, treating as regular message")
                }

                // Regular message
                val senderLabel = if (_remoteUserRole.value == "doctor") "Doctor" else "Patient"
                val senderName = _remoteUserName.value ?: senderLabel

                // Prefix messages consistently
                _messages.value = _messages.value + "patient: $message"
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Only log when necessary to avoid excessive logging
            if (update.status != PayloadTransferUpdate.Status.IN_PROGRESS) {
                Log.d("CureConnect", "🔄 Payload transfer update: ${update.status}")
            }
        }
    }

    private fun sendUserInfo() {
        val localName = _localUserName.value ?: "Anonymous"
        val localRole = _localUserRole.value ?: "patient"

        val userInfo = UserInfo(
            name = localName,
            role = localRole,
            userId = "user-${System.currentTimeMillis()}",
            isInfoMessage = true
        )

        val userInfoJson = gson.toJson(userInfo)

        connectedEndpoint.value?.let { endpointId ->
            try {
                val payload = Payload.fromBytes(userInfoJson.toByteArray())
                Nearby.getConnectionsClient(context)
                    .sendPayload(endpointId, payload)
                    .addOnSuccessListener {
                        Log.d("CureConnect", "User info sent successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("CureConnect", "Failed to send user info: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e("CureConnect", "Error sending user info: ${e.message}")
            }
        }
    }

    fun startConnection() {
        _isConnecting.value = true
        connectionRetryCount = 0

        // Stop any previous connections
        stopAllConnections()

        // Start both advertising and discovery
        startAdvertising()
        startDiscovery()

        // Set a timeout for connecting
        connectionRetryHandler.postDelayed({
            if (_connectedEndpoint.value == null && _isConnecting.value) {
                _isConnecting.value = false
                _connectionError.value = "Connection timed out. Please ensure the other device is nearby."
            }
        }, TimeUnit.SECONDS.toMillis(30)) // 30 second timeout
    }

    private fun stopAllConnections() {
        try {
            Nearby.getConnectionsClient(context).stopAllEndpoints()
            Nearby.getConnectionsClient(context).stopAdvertising()
            Nearby.getConnectionsClient(context).stopDiscovery()
            Log.d("CureConnect", "Stopped all previous connections")
        } catch (e: Exception) {
            Log.e("CureConnect", "Error stopping connections: ${e.message}")
        }
    }

    private fun startDiscovery() {
        viewModelScope.launch {
            try {
                val options = DiscoveryOptions.Builder()
                    .setStrategy(Strategy.P2P_STAR)
                    .build()

                Nearby.getConnectionsClient(context)
                    .startDiscovery(SERVICE_ID, object : EndpointDiscoveryCallback() {
                        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                            Log.d("CureConnect", "✅ Endpoint found: $endpointId - ${info.endpointName}")
                            // Request connection with all discovered endpoints
                            val advertisingName = if (_localUserRole.value == "doctor") {
                                "Doctor:${_localUserName.value ?: ""}"
                            } else {
                                "Patient:${_localUserName.value ?: ""}"
                            }

                            Nearby.getConnectionsClient(context)
                                .requestConnection(advertisingName, endpointId, connectionLifecycleCallback)
                                .addOnSuccessListener {
                                    Log.d("CureConnect", "✅ Connection request sent to $endpointId")
                                }
                                .addOnFailureListener {
                                    Log.e("CureConnect", "❌ Connection request failed: ${it.message}")
                                }
                        }

                        override fun onEndpointLost(endpointId: String) {
                            Log.d("CureConnect", "❌ Lost endpoint: $endpointId")
                        }
                    }, options)
                    .addOnSuccessListener {
                        Log.d("CureConnect", "✅ Discovery started")
                    }
                    .addOnFailureListener {
                        Log.e("CureConnect", "❌ Discovery failed: ${it.message}")
                        _isConnecting.value = false
                        _connectionError.value = "Failed to start discovery: ${it.message}"
                    }
            } catch (e: Exception) {
                Log.e("CureConnect", "Error in startDiscovery: ${e.message}")
                _isConnecting.value = false
                _connectionError.value = "Error starting discovery: ${e.message}"
            }
        }
    }

    private fun startAdvertising() {
        viewModelScope.launch {
            try {
                val options = AdvertisingOptions.Builder()
                    .setStrategy(Strategy.P2P_STAR)
                    .build()

                val advertisingName = if (_localUserRole.value == "doctor") {
                    "Doctor:${_localUserName.value ?: ""}"
                } else {
                    "Patient:${_localUserName.value ?: ""}"
                }

                Nearby.getConnectionsClient(context)
                    .startAdvertising(advertisingName, SERVICE_ID, connectionLifecycleCallback, options)
                    .addOnSuccessListener {
                        Log.d("CureConnect", "✅ Advertising started as $advertisingName")
                    }
                    .addOnFailureListener {
                        Log.e("CureConnect", "❌ Advertising failed: ${it.message}")
                        _isConnecting.value = false
                        _connectionError.value = "Failed to start advertising: ${it.message}"
                    }
            } catch (e: Exception) {
                Log.e("CureConnect", "Error in startAdvertising: ${e.message}")
                _isConnecting.value = false
                _connectionError.value = "Error starting advertising: ${e.message}"
            }
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) {
            return // Don't send empty messages
        }

        connectedEndpoint.value?.let { endpointId ->
            try {
                val payload = Payload.fromBytes(message.toByteArray())
                Nearby.getConnectionsClient(context)
                    .sendPayload(endpointId, payload)
                    .addOnSuccessListener {
                        Log.d("CureConnect", "Message sent successfully")
                        _messages.value = _messages.value + "You: $message"
                    }
                    .addOnFailureListener { e ->
                        Log.e("CureConnect", "Failed to send message: ${e.message}")
                        _connectionError.value = "Failed to send message: ${e.message}"
                    }
            } catch (e: Exception) {
                Log.e("CureConnect", "Error creating or sending payload: ${e.message}")
                _connectionError.value = "Error sending message: ${e.message}"
            }
        } ?: run {
            Log.d("CureConnect", "Cannot send message: Not connected to any endpoint")
            _connectionError.value = "Cannot send message: Not connected to other user"
        }
    }

    fun endConsultation() {
        connectionRetryHandler.removeCallbacksAndMessages(null)
        stopAllConnections()
        _connectedEndpoint.value = null
        _isConnecting.value = false
        _remoteUserName.value = null
        _remoteUserRole.value = null
        _messages.value = emptyList()
    }

    fun clearConnectionError() {
        _connectionError.value = null
        // Retry connection if we got an error
        if (_connectedEndpoint.value == null && !_isConnecting.value) {
            startConnection()
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectionRetryHandler.removeCallbacksAndMessages(null)
        stopAllConnections()
    }
}