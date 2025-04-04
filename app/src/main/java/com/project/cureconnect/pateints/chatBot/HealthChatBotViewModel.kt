package com.project.cureconnect.pateints.chatBot
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.project.cureconnect.R
import com.project.cureconnect.login.UserModel
import com.project.cureconnect.pateints.Api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class HealthChatBotViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        // Add welcome message
        _messages.value = listOf(
            ChatMessage(
                id = "welcome",
                content = "Hello! I'm HealthBot, your personal healthcare assistant. How can I help you today?",
                isFromBot = true
            )
        )
    }

    fun sendMessage(context: Context, message: String, user: UserModel) {
        // Don't send empty messages
        if (message.isBlank()) return

        // Add user message to the chat
        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            content = message,
            isFromBot = false
        )

        // Update the messages list with the user's message
        _messages.value = _messages.value + userMessage

        val payload = JSONObject().apply {
            put("prompt", message)
            put("user_data", JSONObject().apply {
                put("name", user.name ?: "Guest")
                put("medical_history", user?.medicalHistory ?: "No medical history available")
                put("age", user.age ?: "Unknown")

                // Ensure `conditions` is a proper list before converting to JSONArray
                val conditionsList = user.conditions
                put("conditions", JSONArray(conditionsList?.map { it.toString() }))

                // Ensure `medications` is a proper list before converting to JSONArray
                val medicationsList = user.medications
                put("medications", JSONArray(medicationsList?.map { it.toString() }))
            })
        }

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), payload.toString())

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitInstance.responsechat.uploadprompt(requestBody)
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        // Extract the actual content from the responseBody
                        // This assumes your response has a structure like {"response": "actual content"}
                        // Adjust this parsing based on your actual response structure
                        val responseString = responseBody.toString()
                        val content = try {
                            // Try to extract content from the response format
                            if (responseString.contains("response=")) {
                                responseString.substringAfter("response=").trim()
                                    .removeSurrounding("chatmodel(", ")")
                                    .substringAfter("response=")
                                    .removeSurrounding("\"")
                            } else {
                                // Fallback to using the whole response
                                responseString
                            }
                        } catch (e: Exception) {
                            // If parsing fails, use the entire response
                            responseString
                        }

                        val botReply = ChatMessage(
                            id = System.currentTimeMillis().toString(),
                            content = content,
                            isFromBot = true
                        )
                        _messages.value = _messages.value + botReply
                        Log.d("chatbot", "Response: $responseBody")
                    }
                } else {
                    // Handle error by showing an error message in the chat
                    val errorMessage = ChatMessage(
                        id = System.currentTimeMillis().toString(),
                        content = "Sorry, I couldn't process your request. Please try again later.",
                        isFromBot = true
                    )
                    _messages.value = _messages.value + errorMessage
                    Log.d("chatbot", "Error Response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // Show error message in chat
                val errorMessage = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = "Sorry, there was a problem connecting. Please check your connection and try again.",
                    isFromBot = true
                )
                _messages.value = _messages.value + errorMessage
                Log.d("chatbot", "Exception: ${e.message}")
            }
        }
    }
}