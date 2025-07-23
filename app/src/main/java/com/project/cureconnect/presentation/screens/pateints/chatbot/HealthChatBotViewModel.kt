package com.project.cureconnect.presentation.screens.pateints.chatbot
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.project.cureconnect.presentation.screens.AuthScreen.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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
