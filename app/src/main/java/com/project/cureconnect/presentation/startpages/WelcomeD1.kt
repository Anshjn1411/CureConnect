package com.project.cureconnect.presentation.startpages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.project.cureconnect.presentation.navigationRoutes.Screen
import com.project.cureconnect.presentation.screens.AuthScreen.DoctorAuthViewModel
import com.project.cureconnect.ui.theme.BackgroundLight
import com.project.cureconnect.ui.theme.DarkText
import com.project.cureconnect.ui.theme.DividerColor
import com.project.cureconnect.ui.theme.PrimaryBlue
import com.project.cureconnect.ui.theme.*
import com.project.cureconnect.ui.theme.LightText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreenD1(navController: NavController, viewModel: DoctorAuthViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var bioError by remember { mutableStateOf(false) }

    fun validateInputs(): Boolean {
        nameError = name.isBlank()
        emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        phoneError = phone.length != 10 || phone.any { !it.isDigit() }
        bioError = bio.isBlank()

        return !(nameError || emailError || phoneError || bioError)
    }

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
                text = "Personal Information",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Text(
                text = "Step 1 of 3 • Basic Details",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LightText
                ),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LinearProgressIndicator(
                progress = 0.33f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PrimaryBlue,
                trackColor = DividerColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                @Composable
                fun textFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = DividerColor,
                    cursorColor = PrimaryBlue,
                    focusedLabelColor = PrimaryBlue
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    isError = nameError,
                    label = { Text("Full Name", color = TextSecondary) },
                    placeholder = { Text("Enter your full name", color = TextTertiary) },
                    supportingText = {
                        if (nameError) Text("Name is required", color = ErrorRed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    isError = emailError,
                    label = { Text("Email Address", color = TextSecondary) },
                    placeholder = { Text("doctor@example.com", color = TextTertiary) },
                    supportingText = {
                        if (emailError) Text("Please enter a valid email", color = ErrorRed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            phone = it
                            phoneError = false
                        }
                    },
                    isError = phoneError,
                    label = { Text("Phone Number", color = TextSecondary) },
                    placeholder = { Text("1234567890", color = TextTertiary) },
                    supportingText = {
                        if (phoneError) Text("Phone must be 10 digits", color = ErrorRed)
                    },
                    leadingIcon = {
                        Text(
                            text = "+91",
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = {
                        bio = it
                        bioError = false
                    },
                    isError = bioError,
                    label = { Text("Professional Bio", color = TextSecondary) },
                    placeholder = { Text("Tell us about your experience...", color = TextTertiary) },
                    supportingText = {
                        if (bioError) Text("Bio is required", color = ErrorRed)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (validateInputs()) {
                        viewModel.updateDoctor(
                            viewModel.doctorData.value.copy(
                                name = name,
                                email = email,
                                phoneNumber = phone,
                                bio = bio
                            )
                        )
                        navController.navigate(Screen.WelcomeScreenD2.routes)
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

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

