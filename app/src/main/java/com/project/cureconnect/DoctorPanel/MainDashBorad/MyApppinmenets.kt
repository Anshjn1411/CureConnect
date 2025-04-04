package com.project.cureconnect.DoctorPanel.MainDashBorad

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.cureconnect.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen(
    navController: NavController,
    doctorId: String? = null,
    appointmentViewModel: DoctorAppointmentViewModel = viewModel()
) {
    // Set doctor ID and fetch appointments if provided
    LaunchedEffect(doctorId) {
        doctorId?.let {
            if (it.isNotEmpty() && appointmentViewModel.currentDoctorId.value != it) {
                appointmentViewModel.setDoctorId(it)
            }
        }
    }

    // Collect appointments from ViewModel
    val appointments by appointmentViewModel.appointments.collectAsState()
    val isLoading by remember { appointmentViewModel.isLoading }
    val errorMessage by appointmentViewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Today") }

    // Get current date for the header
    val currentDate = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")) }

    // Filter appointments based on tab and search
    val filteredAppointments = when (selectedTab) {
        "Today" -> appointments.filter { it.date == LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) }
        "Upcoming" -> appointments.filter {
            try {
                val appointmentDate = LocalDate.parse(it.date, DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                appointmentDate.isAfter(LocalDate.now())
            } catch (e: Exception) {
                false
            }
        }
        else -> appointments
    }.filter {
        if (searchQuery.isNotEmpty()) {
            it.patientName.contains(searchQuery, ignoreCase = true) ||
                    it.reason.contains(searchQuery, ignoreCase = true)
        } else true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Appointments", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show error message if any
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // Date header
            Text(
                text = currentDate,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search patients or conditions") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            // Tabs for Today/Upcoming appointments
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                listOf("Today", "Upcoming").forEach { tab ->
                    val isSelected = selectedTab == tab

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = tab,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (isSelected) Color(0xFF4285F4) else Color(0xFFF5F7FA),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(vertical = 12.dp)
                                .clickable { selectedTab = tab },
                            color = if (isSelected) Color.White else Color.Gray,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading appointments...")
                    }
                }
            } else if (appointments.isEmpty()) {
                // No appointments view
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "No Appointments",
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF4285F4)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No appointments found for ${selectedTab.lowercase()}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { appointmentViewModel.refreshAppointments() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4)
                            )
                        ) {
                            Text("Refresh Appointments", fontSize = 16.sp)
                        }
                    }
                }
            } else {
                // Appointment count summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You have ${appointments.size} appointment(s) ${if (selectedTab == "Today") "today" else "upcoming"}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF4285F4)
                        )
                    }
                }

                // List of appointments
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(appointments) { appointment ->
                        PatientAppointmentCard(
                            appointment = appointment,
                            navController = navController,
                            appointmentViewModel = appointmentViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientAppointmentCard(
    appointment: PatientAppointment,
    navController: NavController,
    appointmentViewModel: DoctorAppointmentViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Patient info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Patient Image
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F7FA))
                ) {
                    Image(
                        painter = painterResource(R.drawable.user_logo),
                        contentDescription = "Patient Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Patient name and reason
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = appointment.patientId,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = appointment.reason,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Status chip
                Box(
                    modifier = Modifier
                        .background(
                            color = when(appointment.status) {
                                "Scheduled" -> Color(0xFFE8F5E9)
                                "Confirmed" -> Color(0xFFE1F5FE)
                                "Pending" -> Color(0xFFFFF8E1)
                                "Rescheduled" -> Color(0xFFF1F8E9)
                                "Cancelled" -> Color(0xFFFFEBEE)
                                "Completed" -> Color(0xFFE0F2F1)
                                else -> Color(0xFFF5F7FA)
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = appointment.status,
                        fontSize = 12.sp,
                        color = when(appointment.status) {
                            "Scheduled" -> Color(0xFF43A047)
                            "Confirmed" -> Color(0xFF0288D1)
                            "Pending" -> Color(0xFFFFA000)
                            "Rescheduled" -> Color(0xFF7CB342)
                            "Cancelled" -> Color(0xFFE53935)
                            "Completed" -> Color(0xFF009688)
                            else -> Color.Gray
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Divider(
                color = Color(0xFFF5F7FA),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Date and time info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Date",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = appointment.date,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Time column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Time",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = appointment.time,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Different actions based on appointment status
                when (appointment.status) {
                    "Scheduled", "Confirmed" -> {
                        // Cancel button
                        OutlinedButton(
                            onClick = { appointmentViewModel.cancelAppointment(appointment.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, Color.Red),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Cancel")
                        }

                        // Start button for consultation
                        Button(
                            onClick = { navController.navigate("patient_consultation/${appointment.patientId}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4)
                            )
                        ) {
                            Text("Start")
                        }
                    }
                    "Pending" -> {
                        // Confirm button
                        Button(
                            onClick = { appointmentViewModel.confirmAppointment(appointment.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4)
                            )
                        ) {
                            Text("Confirm")
                        }

                        // Cancel button
                        OutlinedButton(
                            onClick = { appointmentViewModel.cancelAppointment(appointment.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, Color.Red),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Cancel")
                        }
                    }
                    else -> {
                        // View details button for all other statuses
                        Button(
                            onClick = { navController.navigate("patient_consultation/${appointment.patientId}") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4)
                            )
                        ) {
                            Text("View Details")
                        }
                    }
                }
            }
        }
    }
}