package com.project.cureconnect.presentation.screens.pateints.CardScreen.consult

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import kotlinx.coroutines.flow.StateFlow


data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private var listener: ListenerRegistration? = null

    fun listenToMessages(doctorId: String, userId: String) {
        val chatId = generateChatId(doctorId, userId)

        listener?.remove() // remove previous listener
        listener = db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatViewModel", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val msgList = it.documents.mapNotNull { doc ->
                        doc.toObject(ChatMessage::class.java)
                    }
                    _messages.value = msgList
                }
            }
    }

    fun sendMessage(doctorId: String, userId: String, message: String) {
        val chatId = generateChatId(doctorId, userId)
        val msg = ChatMessage(message = message, senderId = userId)

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(msg)
            .addOnFailureListener {
                Log.e("ChatViewModel", "Failed to send message: ${it.message}")
            }
    }

    private fun generateChatId(doctorId: String, userId: String): String {
        return if (doctorId < userId) "${doctorId}_$userId" else "${userId}_$doctorId"
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}