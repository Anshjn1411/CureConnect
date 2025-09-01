package com.project.cureconnect.presentation.startpages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.presentation.navigationRoutes.Screen
import com.project.cureconnect.presentation.screens.AuthScreen.DoctorAuthViewModel
import com.project.cureconnect.ui.theme.BackgroundLight
import com.project.cureconnect.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WelcomeScreenD2(navController: NavController, viewModel: DoctorAuthViewModel) {
    val specialties = listOf(
        "General Physician", "Cardiologist", "Dermatologist", "Neurologist",
        "Orthopedic", "Pediatrician", "Psychiatrist", "Radiologist",
        "Dentist", "Oncologist", "Gynecologist", "ENT Specialist",
        "Urologist", "Gastroenterologist", "Ophthalmologist"
    )

    val timeSlots = listOf(
        "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM",
        "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM"
    )

    var specialty by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var fee by remember { mutableStateOf("") }
    var upi by remember { mutableStateOf("") }
    var selectedTimes by remember { mutableStateOf(setOf<String>()) }
    var showError by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Professional Details",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Text(
                text = "Step 2 of 3 • Practice Information",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LightText
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LinearProgressIndicator(
                progress = 0.66f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PrimaryBlue,
                trackColor = DividerColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = specialty,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Medical Specialty", color = TextSecondary) },
                            placeholder = { Text("Select your specialty", color = TextTertiary) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = DividerColor,
                                cursorColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            specialties.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        specialty = item
                                        expanded = false
                                        showError = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = fee,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                fee = it
                                showError = false
                            }
                        },
                        label = { Text("Consultation Fee", color = TextSecondary) },
                        placeholder = { Text("500", color = TextTertiary) },
                        leadingIcon = {
                            Text(
                                text = "₹",
                                color = TextSecondary,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DividerColor,
                            cursorColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = upi,
                        onValueChange = {
                            upi = it
                            showError = false
                        },
                        label = { Text("UPI ID", color = TextSecondary) },
                        placeholder = { Text("doctor@paytm", color = TextTertiary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = DividerColor,
                            focusedLabelColor = PrimaryBlue,
                            containerColor = BackgroundWhite
                        ),
                        singleLine = true
                    )
                }

                item {
                    Column {
                        Text(
                            text = "Available Time Slots",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Select your available consultation hours",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LightText
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            timeSlots.forEach { slot ->
                                val isSelected = selectedTimes.contains(slot)
                                FilterChip(
                                    onClick = {
                                        selectedTimes = if (isSelected) {
                                            selectedTimes - slot
                                        } else {
                                            selectedTimes + slot
                                        }
                                        showError = false
                                    },
                                    label = { Text(slot, fontSize = 13.sp) },
                                    selected = isSelected,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryBlue,
                                        selectedLabelColor = BackgroundWhite,
                                        containerColor = BackgroundWhite,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) PrimaryBlue else DividerColor,
                                        selectedBorderColor = PrimaryBlue
                                    )
                                )
                            }
                        }
                    }
                }

                if (showError) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                            border = BorderStroke(1.dp, ErrorRed)
                        ) {
                            Text(
                                text = "Please fill in all required fields",
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (specialty.isBlank() || fee.isBlank() || upi.isBlank() || selectedTimes.isEmpty()) {
                                showError = true
                            } else {
                                showError = false
                                viewModel.updateDoctor(
                                    viewModel.doctorData.value.copy(
                                        specialty = specialty,
                                        consultationFee = fee.toDoubleOrNull() ?: 0.0,
                                        upiId = upi,
                                        availableTimes = selectedTimes.toList()
                                    )
                                )
                                navController.navigate(Screen.WelcomeScreenD3.routes)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = BackgroundWhite
                        )
                    ) {
                        Text(
                            text = "Continue",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
