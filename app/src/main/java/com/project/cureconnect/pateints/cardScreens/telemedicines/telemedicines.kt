package com.project.cureconnect.patients.cardScreens.telemedicines

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

@Composable
fun telemedicineScreen(navController: NavController) {
    val context = LocalContext.current
    var roomName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun launchJitsiMeeting(room: String) {
        try {
            isLoading = true
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(room)
                .setAudioMuted(true)
                .setVideoMuted(true)
                .setAudioOnly(false)
                .setFeatureFlag("pip.enabled", true)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("live-streaming.enabled", false)
                .setFeatureFlag("recording.enabled", false)
                .build()

            JitsiMeetActivity.launch(context, options)

            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                isLoading = false
            }
        } catch (e: Exception) {
            isLoading = false
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .size(28.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Telemedicine", fontSize = 20.sp, color = Color.Black)
        }

        // Video icon and info
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Secure Video Call", fontSize = 18.sp, color = Color.Black)
            Text("Consult your doctor instantly", fontSize = 14.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Room Name Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = roomName,
                onValueChange = { roomName = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                decorationBox = { innerTextField ->
                    if (roomName.isBlank()) {
                        Text("Enter Room ID", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Join Meeting Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    if (roomName.isNotBlank()) Color(0xFF4CAF50) else Color.LightGray,
                    RoundedCornerShape(10.dp)
                )
                .clickable(enabled = roomName.isNotBlank() && !isLoading) {
                    launchJitsiMeeting(roomName.trim())
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isLoading) "Connecting..." else "Join Meeting",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        // Quick Join
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFEEEEEE), RoundedCornerShape(10.dp))
                .clickable(enabled = !isLoading) {
                    val randomRoom = generateRandomRoomName()
                    roomName = randomRoom
                    launchJitsiMeeting(randomRoom)
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Quick Join", fontSize = 16.sp, color = Color.Black)
        }

        // Meeting Info
        Spacer(modifier = Modifier.height(32.dp))
        Column {
            Text("Meeting Info", fontSize = 16.sp, color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = """
                    • Audio/Video muted by default
                    • End-to-end encrypted
                    • Use same room ID to connect
                    • Picture-in-Picture supported
                """.trimIndent(),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

private fun generateRandomRoomName(): String {
    val adjectives = listOf("swift", "bright", "calm", "safe", "secure", "private")
    val nouns = listOf("room", "space", "meeting", "call", "session", "chat")
    val randomNum = (1000..9999).random()
    return "${adjectives.random()}-${nouns.random()}-$randomNum"
}
