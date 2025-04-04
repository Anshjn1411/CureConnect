package com.project.cureconnect.pateints.cardScreens.consult

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.cureconnect.DoctorPanel.cardScreen.consut.RequestPermissions
import com.project.cureconnect.DoctorPanel.cardScreen.consut.checkConnectivityStatus
import com.project.cureconnect.primaryBlue
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission", "StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ChatScreen(navController: NavController, viewModel: ChatViewModel = viewModel()) {
    val context = LocalContext.current
    RequestPermissions(viewModel)

    val (bluetoothEnabled, locationEnabled) = checkConnectivityStatus(context)
    var showPermissionDialog by remember { mutableStateOf(!bluetoothEnabled || !locationEnabled) }

    LaunchedEffect(Unit) {
        viewModel.setContext(context)
    }

    val messages by viewModel.messages.collectAsState()
    val connectedEndpoint by viewModel.connectedEndpoint.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val connectionError by viewModel.connectionError.collectAsState()
    var message by remember { mutableStateOf("") }

    // Handle Bluetooth and Location service dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { /* Cannot dismiss by clicking outside */ },
            title = { Text("Required Services") },
            text = {
                Column {
                    if (!bluetoothEnabled) {
                        Text("Please enable Bluetooth for the offline consultation to work")
                    }
                    if (!locationEnabled) {
                        Text("Please enable Location services for device discovery")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (!bluetoothEnabled) {
                        context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }
                    if (!locationEnabled) {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    showPermissionDialog = false
                }) {
                    Text("Open Settings")
                }
            }
        )
    }

    // Connection error dialog
    if (connectionError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearConnectionError() },
            title = { Text("Connection Error") },
            text = { Text(connectionError!!) },
            confirmButton = {
                Button(onClick = { viewModel.clearConnectionError() }) {
                    Text("Try Again")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if(isConnecting) "CureConnect" else "Patient ${viewModel.remoteUserName.value}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBlue,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Connection status card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (connectedEndpoint != null) {
                            Text(
                                "✓ Connected to healthcare provider",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A8754)
                            )
                            Text(
                                "Secure offline consultation in progress",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        } else {
                            Text(
                                "Waiting for healthcare provider",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Please wait...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (connectedEndpoint != null) {
                        Button(
                            onClick = { viewModel.endConsultation() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text("End")
                        }
                    } else if (!isConnecting) {
                        Button(
                            onClick = { viewModel.startConnection() }
                        ) {
                            Text("Connect")
                        }
                    }
                }
            }

            // Chat messages area
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    if (messages.isEmpty()) {
                        // Empty state
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (connectedEndpoint != null)
                                    "Start your consultation by sending a message"
                                else
                                    "Waiting for connection to healthcare provider...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            reverseLayout = true
                        ) {
                            items(messages.asReversed()) { msg ->
                                // Check if the message is empty before processing
                                if (msg.isEmpty()) {
                                    // Handle empty message case
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Empty message",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                } else {
                                    // Process valid messages
                                    val isUserMessage = msg.startsWith("You: ")
                                    val isSystemMessage = msg.startsWith("System: ")
                                    val messageText = when {
                                        isUserMessage && msg.length > 5 -> msg.substring(5)
                                        isSystemMessage && msg.length > 8 -> msg.substring(8)
                                        msg.length > 7 -> msg.substring(7) // Doctor message
                                        else -> msg // Fallback for messages that don't match expected format
                                    }

                                    if (isSystemMessage) {
                                        // System message centered
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = messageText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    } else {
                                        // User or doctor message
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            contentAlignment = if (isUserMessage) Alignment.CenterEnd else Alignment.CenterStart
                                        ) {
                                            Card(
                                                modifier = Modifier
                                                    .widthIn(max = 280.dp)
                                                    .padding(vertical = 2.dp),
                                                shape = RoundedCornerShape(
                                                    topStart = 12.dp,
                                                    topEnd = 12.dp,
                                                    bottomStart = if (isUserMessage) 12.dp else 4.dp,
                                                    bottomEnd = if (isUserMessage) 4.dp else 12.dp
                                                ),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isUserMessage)
                                                        primaryBlue
                                                    else
                                                        MaterialTheme.colorScheme.surfaceVariant
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp)
                                                ) {
                                                    Text(
                                                        text = if (isUserMessage) "You" else "Doctor",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (isUserMessage)
                                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                                        else
                                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                                    )

                                                    Spacer(modifier = Modifier.height(2.dp))

                                                    Text(
                                                        text = messageText,
                                                        color = if (isUserMessage)
                                                            MaterialTheme.colorScheme.onPrimary
                                                        else
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Message input area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("Type your message...") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (message.isNotBlank() && connectedEndpoint != null) {
                                viewModel.sendMessage(message)
                                message = ""
                            }
                        }),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        enabled = connectedEndpoint != null
                    )

                    Button(
                        onClick = {
                            if (message.isNotBlank() && connectedEndpoint != null) {
                                viewModel.sendMessage(message)
                                message = ""
                            }
                        },
                        enabled = message.isNotBlank() && connectedEndpoint != null
                    ) {
                        Text("Send")
                    }
                }
            }
        }
    }
}