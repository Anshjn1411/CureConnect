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
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.project.cureconnect.login.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content


data class ChatMessage(
    val id: String,
    val content: String,
    val isFromBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class HealthChatBotViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyASSY9fkUZY2Q9cYsCd-mTMK0sr98lPh30" // Replace this with your API key securely
    )

    init {
        _messages.value = listOf(
            ChatMessage(
                id = "welcome",
                content = "Hello! I'm HealthBot, your personal healthcare assistant. How can I help you today?",
                isFromBot = true
            )
        )
    }

    fun sendMessage(context: Context, message: String, user: User) {
        if (message.isBlank()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            content = message,
            isFromBot = false
        )
        _messages.value = _messages.value + userMessage

        val fullPrompt = buildPrompt(user, message)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(fullPrompt)

                val replyText = response.text ?: "Sorry, I couldn't understand. Please try again."

                val botReply = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = replyText,
                    isFromBot = true
                )

                _messages.value = _messages.value + botReply
            } catch (e: Exception) {
                val errorReply = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = "Something went wrong. Please try again.",
                    isFromBot = true
                )
                _messages.value = _messages.value + errorReply
            }
        }

    }

    private fun buildPrompt(user: User, question: String): String {
        val name = user.name ?: "User"
        val age = user.age ?: "Unknown"


        return """
            You are HealthBot, an intelligent and friendly AI healthcare assistant. You help users by answering questions, explaining symptoms, suggesting healthy practices, and guiding them on wellness topics.

            Patient Name: $name
            Age: $age

            User's Message:
            "$question"

            Respond clearly and empathetically, based on the user's context.
        """.trimIndent()
    }
}
