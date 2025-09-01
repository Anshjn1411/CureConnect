package com.project.cureconnect.presentation.screens.pateints.MainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmergencyShare
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.R
import com.project.cureconnect.lightGrey
import com.project.cureconnect.primaryBlue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.project.cureconnect.changeLanguage
import com.project.cureconnect.getSavedLanguage
import com.project.cureconnect.presentation.screens.pateints.viewmodel.SpeechRecognitionHelper
import com.project.cureconnect.presentation.navigationRoutes.Screen
import com.project.cureconnect.ui.theme.BottomSheetShape
import com.project.cureconnect.ui.theme.Elevations

@Composable
fun MainTopBar(navController: NavController, speechHelper: SpeechRecognitionHelper) {
    val context = LocalContext.current
    val languages = listOf("English", "हिन्दी", "मराठी")
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(getSavedLanguage(context)) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = Elevations.medium,
        shape = BottomSheetShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔷 Logo & App Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.cureconnect_logo),
                    contentDescription = "CureConnect Logo",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "CureConnect",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // ⚙️ Actions: Notification, Emergency, Language
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    navController.navigate(Screen.MyAppointment.routes)
                }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = {
                    navController.navigate("IVR")
                }) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmergencyShare,
                            contentDescription = "Emergency",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 🌐 Language Dropdown
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Select Language",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = language,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    selectedLanguage = language
                                    changeLanguage(
                                        context,
                                        when (language) {
                                            "English" -> "en"
                                            "हिन्दी" -> "hi"
                                            "मराठी" -> "mr"
                                            else -> "en"
                                        }
                                    )
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
