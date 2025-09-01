package com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet

import android.content.Context
import android.graphics.fonts.FontStyle
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.cureconnect.R
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(
    navController: NavController,
    appoinmenetViewModel: AppoinmenetViewModel
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val state by appoinmenetViewModel.state.collectAsState()

    // Load appointments once
    LaunchedEffect(Unit) {
        appoinmenetViewModel.loadUserUser(sessionManager)

        appoinmenetViewModel.userData
            .filterNotNull()
            .firstOrNull()
            ?.uid?.let { uid ->
                appoinmenetViewModel.loadAppointments(uid)
            }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Appointments") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.appointments.isEmpty() -> {
                EmptyAppointmentsUI(navController, paddingValues)
            }

            else -> {
                AppointmentsListUI(
                    appointments = state.appointments,
                    doctorMap = state.doctorMap,
                    onCancel = { appoinmenetViewModel.cancelAppointment(it) {} },
                    navController = navController,
                    paddingValues = paddingValues,
                    appoinmenetViewModel
                )
            }
        }
    }
}
@Composable
fun AppointmentCard(
    appointment: Appointment,
    doctor: Doctor?,
    onCancel: (Appointment) -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Date: ${appointment.date}")
            Text("Time: ${appointment.time}")

            if (doctor != null) {
                Text("Doctor: Dr. ${doctor.name}")
                Text("Specialty: ${doctor.specialty}")
            } else {
                Text("Doctor info not available...")
            }

            Button(onClick = { onCancel(appointment) }) {
                Text("Cancel Appointment")
            }
        }
    }
}


@Composable
fun AppointmentsListUI(
    appointments: List<Appointment>,
    doctorMap: Map<String, Doctor>,
    onCancel: (Appointment) -> Unit,
    navController: NavController,
    paddingValues: PaddingValues,
    appoinmenetViewModel: AppoinmenetViewModel // Pass ViewModel here
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        appointments.forEach { appointment ->
            val doctorId = appointment.doctorId
            val doctor = doctorMap[doctorId]

            // Trigger fetch if not present
            if (doctor == null) {
                LaunchedEffect(doctorId) {
                    appoinmenetViewModel.fetchDoctorIfNeeded(doctorId)
                }
            }

            AppointmentCard(
                appointment = appointment,
                doctor = doctor,
                onCancel = onCancel,
                navController = navController
            )
        }
    }
}


@Composable
fun EmptyAppointmentsUI(navController: NavController, paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No appointments found")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("available_doctors") }) {
                Text("Book Appointment")
            }
        }
    }
}
