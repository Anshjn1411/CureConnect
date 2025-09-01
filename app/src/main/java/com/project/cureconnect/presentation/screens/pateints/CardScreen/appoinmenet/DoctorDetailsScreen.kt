package com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet

import PaymentViewModel
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.project.cureconnect.R
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager

import com.project.cureconnect.ui.theme.CureConnectTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailsScreen(
    doctor: Doctor,
    navController: NavController,
    viewModel: AppoinmenetViewModel
) {
    val scrollState = rememberScrollState()
    var selectedDay by remember { mutableStateOf(LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())) }
    var selectedDate by remember { mutableStateOf(LocalDate.now().dayOfMonth.toString()) }
    var selectedTime by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as ComponentActivity
    var appointments by remember { mutableStateOf<Appointment?>(null) }
    val sessionManager= UserSessionManager(context)

    val user by viewModel.userData.collectAsState()
    val history by viewModel.history.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserAndHistory(sessionManager)
    }


    val paymentViewModel: PaymentViewModel = viewModel()
    val paymentStatus by paymentViewModel.paymentStatus.collectAsState()

    LaunchedEffect(paymentStatus) {
        when (val status = paymentStatus) {
            is PaymentStatus.Success -> {
                Toast.makeText(context, "Payment Successful!", Toast.LENGTH_SHORT).show()
                navController.navigate("appointment_confirmation/${doctor.uid}") {
                    popUpTo("book_appointment") { inclusive = true }
                }
            }
            is PaymentStatus.Failed -> {
                Toast.makeText(context, "Payment Failed: ${status.errorMessage}", Toast.LENGTH_SHORT).show()
            }
            PaymentStatus.Initial -> {
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Doctor Detail",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Doctor Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Doctor Image
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.doctor1),
                            contentDescription = "Doctor Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Doctor Info
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = doctor.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = doctor.specialty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = "4.7",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Distance",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                text = "800m away",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            // About Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = doctor.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

            }

            // UI oF THE TIME SLOT ___ WITH Realtime Dates and Availbel Times of Doctor
            AvailableDateAndTimeSection(
                doctor = doctor,
                selectedDay = selectedDay,
                selectedDate = selectedDate,
                onDateSelected = { day, date ->
                    selectedDay = day
                    selectedDate = date
                },
                selectedTime = selectedTime,
                onTimeSelected = { time -> selectedTime = time }
            )


            Spacer(modifier = Modifier.height(24.dp))

            // Book Appointment Button
            Button(
                onClick = {
                    val appointment = Appointment(
                        id = UUID.randomUUID().toString(),
                        patientId = user?.uid.toString(),
                        doctorId = doctor.uid,
                        doctorname = doctor.name,
                        date = "$selectedDay $selectedDate",
                        time = selectedTime,
                        status = "Pending Payment",
                        patientHistoryRecord = history
                    )

                    paymentViewModel.initiatePayment(
                        activity,
                        appointment,
                        doctor,
                    )

                    viewModel.successfullBooking(user?.email, appointment, doctor, name)
                    bookAppointment(appointment)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Book Appointment",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun AvailableDateAndTimeSection(
    doctor: Doctor,
    selectedDay: String,
    selectedDate: String,
    onDateSelected: (String, String) -> Unit,
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    val days = remember {
        val today = LocalDate.now()
        (0..4).map { offset ->
            val date = today.plusDays(offset.toLong())
            Pair(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()), date.dayOfMonth.toString())
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {

        // 📅 Days Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            items(days) { (day, date) ->
                DayItem(
                    day = day,
                    date = date,
                    isSelected = selectedDate == date,
                    onClick = { onDateSelected(day, date) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🕒 Time Slots Grid
        val chunkedTimes = doctor.availableTimes.chunked(3)

        chunkedTimes.forEach { rowTimes ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowTimes.forEach { time ->
                    TimeSlot(
                        time = time,
                        isSelected = selectedTime == time,
                        doctor = doctor,
                        onClick = { onTimeSelected(time) }
                    )
                }

                repeat(3 - rowTimes.size) {
                    Spacer(modifier = Modifier.width(110.dp))
                }
            }
        }
    }
}


@Composable
fun DayItem(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(80.dp)
            .clip(MaterialTheme.shapes.small)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = date,
                style = MaterialTheme.typography.headlineSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TimeSlot(time: String, isSelected: Boolean, doctor: Doctor, onClick: () -> Unit) {
    val isAvailable = doctor.availableTimes.contains(time)

    Box(
        modifier = Modifier
            .width(110.dp)
            .height(40.dp)
            .clip(MaterialTheme.shapes.large)
            .background(
                when {
                    isSelected && isAvailable -> MaterialTheme.colorScheme.primary
                    isAvailable -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.large
            )
            .clickable(enabled = isAvailable, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isAvailable -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            }
        )
    }
}


