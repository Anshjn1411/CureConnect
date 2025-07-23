package com.project.cureconnect.patients.cardScreens.consult

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.cureconnect.presentation.screens.pateints.CardScreen.consult.ChatViewModel
import com.project.cureconnect.ui.theme.PrimaryBlue
import com.project.cureconnect.ui.theme.PrimaryBlueLight

@Composable
fun ChatScreen(
    navController: NavController,
    doctorId: String,
    userId: String,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Start listening to messages
    LaunchedEffect(doctorId, userId) {
        viewModel.listenToMessages(doctorId, userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
    ) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(end = 16.dp),
                tint = Color.Black
            )
            Text(
                text = "Chat with Doctor",
                color = Color.Black,
                fontSize = 20.sp
            )
        }

        // Chat messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                ChatBubble(message = msg.message, isUser = msg.senderId == userId)
            }
        }

        // Input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.text.BasicTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendMessage(doctorId, userId, inputMessage)
                            inputMessage = ""
                            focusManager.clearFocus()
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    if (inputMessage.isBlank()) {
                        Text("Type message...", color = Color.Gray, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = PrimaryBlueLight,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendMessage(doctorId, userId, inputMessage)
                            inputMessage = ""
                            focusManager.clearFocus()
                        }
                    }
            )
        }
    }
}


@Composable
fun ChatBubble(message: String, isUser: Boolean) {
    val bubbleColor = if (isUser) PrimaryBlue else Color(0xFFE0E0E0)
    val textColor = if (isUser) Color.White else Color.Black
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val horizontalPadding = if (isUser) 64.dp else 8.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(color = bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = message,
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}
