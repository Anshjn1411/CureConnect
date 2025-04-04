package com.project.cureconnect.pateints.mainScreens

import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

// Step 2: Updated MainTopBar with integrated speech recognition
@Composable
fun MainTopBar(navController: NavController, speechHelper: SpeechRecognitionHelper) {
    val context = LocalContext.current
    val languages = listOf("English", "हिन्दी", "मराठी") // Added Marathi option
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(getSavedLanguage(context)) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo & App Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.cureconnect_logo),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "CureConnect",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }

            // Action Icons + Language Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.navigate("my_appointments") }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.DarkGray
                    )
                }

                IconButton(onClick = { navController.navigate("IVR") }) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(lightGrey),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmergencyShare,
                            contentDescription = "Profile",
                            tint = Color.DarkGray
                        )
                    }
                }

                // 🌐 Language Selector Button
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.Language, // Use a globe/language icon
                            contentDescription = "Select Language",
                            tint = Color.DarkGray
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(text = language) },
                                onClick = {
                                    selectedLanguage = language
                                    changeLanguage(
                                        context,
                                        when (language) {
                                            "English" -> "en"
                                            "हिन्दी" -> "hi"
                                            "मराठी" -> "mr" // Marathi language code
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
