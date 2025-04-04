package com.project.cureconnect.pateints.cardScreens.appoinmenet

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.cureconnect.R

import kotlinx.coroutines.launch
import login.AuthViewModel

import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailsScreen(doctor: Doctor, navController: NavController, viewModelLogin: AuthViewModel = viewModel(), viewModel: AppoinmenetViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    var selectedDate by remember { mutableStateOf("23") }
    var selectedDay by remember { mutableStateOf("Wed") }
    var selectedTime by remember { mutableStateOf("02:00 PM") }
    val email: String = viewModelLogin.getCurrentUserEmail().toString()
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as ComponentActivity
    var appointments by remember { mutableStateOf<Appointment?>(null) }
    viewModelLogin.getUserData { user ->
        name = user?.name.toString()
    }
    Log.d("checker ", "checking")

    val paymentViewModel: PaymentViewModel = viewModel()
    val paymentStatus by paymentViewModel.paymentStatus.collectAsState()
// In your effect or side-effect handler
    LaunchedEffect(paymentStatus) {
        when (val status = paymentStatus) {
            is PaymentStatus.Success -> {
                Log.d("aaaaaaaa", "khwgdbkdbdb")
                // Show success toast
                Toast.makeText(context, "Payment Successful!", Toast.LENGTH_SHORT).show()

                // Navigate to confirmation screen
                navController.navigate("appointment_confirmation") {
                    popUpTo("book_appointment") { inclusive = true }
                }
            }
            is PaymentStatus.Failed -> {
                // Show error toast or dialog
                Toast.makeText(context, "Payment Failed: ${status.errorMessage}", Toast.LENGTH_SHORT).show()
            }
            PaymentStatus.Initial -> {
                // Initial state, do nothing
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Detail", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
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
                .verticalScroll(scrollState)
        ) {
            // Doctor Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F7FA))
                    ) {
                        Image(
                            painter = painterResource( doctor.imageRes),
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
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = doctor.specialty,
                            fontSize = 14.sp,
                            color = Color.Gray,
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
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Distance",
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                text = "800m away",
                                fontSize = 14.sp,
                                color = Color.Gray,
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipi elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam...",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Read more",
                    fontSize = 14.sp,
                    color = Color(0xFF4285F4),
                    modifier = Modifier.padding(top = 4.dp).clickable{}

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Week View
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DayItem("Mon", "21", selectedDay == "Mon" && selectedDate == "21") {
                    selectedDay = "Mon"
                    selectedDate = "21"
                }
                DayItem("Tue", "22", selectedDay == "Tue" && selectedDate == "22") {
                    selectedDay = "Tue"
                    selectedDate = "22"
                }
                DayItem("Wed", "23", selectedDay == "Wed" && selectedDate == "23") {
                    selectedDay = "Wed"
                    selectedDate = "23"
                }
                DayItem("Thu", "24", selectedDay == "Thu" && selectedDate == "24") {
                    selectedDay = "Thu"
                    selectedDate = "24"
                }
                DayItem("Fri", "25", selectedDay == "Fri" && selectedDate == "25") {
                    selectedDay = "Fri"
                    selectedDate = "25"
                }
                DayItem("Sat", "26", selectedDay == "Sat" && selectedDate == "26") {
                    selectedDay = "Sat"
                    selectedDate = "26"
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Time Slots - Row 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeSlot("09:00 AM", selectedTime == "09:00 AM" , doctor) { selectedTime = "09:00 AM" }
                TimeSlot("10:00 AM", selectedTime == "10:00 AM" , doctor) { selectedTime = "10:00 AM" }
                TimeSlot("11:00 AM", selectedTime == "11:00 AM" , doctor) { selectedTime = "11:00 AM" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Slots - Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeSlot("01:00 PM", selectedTime == "01:00 PM" , doctor) { selectedTime = "01:00 PM" }
                TimeSlot("02:00 PM", selectedTime == "02:00 PM", doctor) { selectedTime = "02:00 PM" }
                TimeSlot("03:00 PM", selectedTime == "03:00 PM" , doctor) { selectedTime = "03:00 PM" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Slots - Row 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeSlot("04:00 PM", selectedTime == "04:00 PM" , doctor) { selectedTime = "04:00 PM" }
                TimeSlot("07:00 PM", selectedTime == "07:00 PM" , doctor) { selectedTime = "07:00 PM" }
                TimeSlot("08:00 PM", selectedTime == "08:00 PM", doctor) { selectedTime = "08:00 PM" }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Book Appointment Button
            Button(

                onClick = {
                    val appointment = Appointment(
                        id = UUID.randomUUID().toString(),
                        patientId = name,
                        doctorId = doctor.id,
                        doctorname = doctor.name,
                        date = "$selectedDay $selectedDate",
                        time = selectedTime,
                        status = "Pending Payment"
                    )

                    paymentViewModel.initiatePayment(
                        activity,
                        appointment,
                        doctor,
                    )

                    viewModel.successfullBooking(email, appointment, doctor,name)
                    bookAppointment(appointment)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4)
                )
            ) {
                Text("Book Appointment", fontSize = 16.sp)
            }
        }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }


@Composable
fun DayItem(day: String, date: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF4285F4) else Color(0xFFF5F7FA))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color.Gray
            )
            Text(
                text = date,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun TimeSlot(time: String, isSelected: Boolean,doctor: Doctor, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected && doctor.availableTimes.contains(time)) Color(0xFF4285F4)
                else if(doctor.availableTimes.contains(time)) Color(0xFFF5F7FA)
                else Color(0xFF64B9F8)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF4285F4) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}




/**
 * Sealed class representing different states of the payment process
 */
sealed class PaymentState {
    /**
     * Represents the initial state before any payment action
     */
    object Idle : PaymentState()

    /**
     * Represents a successful payment and appointment booking
     * @property appointment The successfully booked appointment details
     */
    data class Success(val appointment: Appointment) : PaymentState()

    /**
     * Represents an error during the payment or booking process
     * @property message Detailed error message describing the failure
     */
    data class Error(val message: String) : PaymentState()

    /**
     * Represents the ongoing payment processing state
     */
    object Processing : PaymentState()

    /**
     * Represents a pending payment state
     * @property transactionId Unique identifier for the pending transaction
     */
    data class Pending(val transactionId: String) : PaymentState()

    /**
     * Represents a payment cancellation state
     */
    object Cancelled : PaymentState()

    /**
     * Checks if the current state represents a successful payment
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Checks if the current state represents an error
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Checks if the current state is processing
     */
    val isProcessing: Boolean
        get() = this == Processing

    /**
     * Provides a user-friendly message based on the current state
     */
    fun getUserMessage(): String = when (this) {
        is Idle -> "Payment not initiated"
        is Success -> "Payment successful for appointment ${appointment.id}"
        is Error -> message
        is Processing -> "Processing payment..."
        is Pending -> "Payment pending for transaction $transactionId"
        is Cancelled -> "Payment was cancelled"
    }
}

/**
 * Companion object to provide additional utility methods for PaymentState
 */
object PaymentStateUtils {
    /**
     * Create a specific error state with predefined error types
     */
    fun createErrorState(type: ErrorType, customMessage: String? = null): PaymentState.Error {
        return PaymentState.Error(
            customMessage ?: when (type) {
                ErrorType.NETWORK_ERROR -> "Network connection failed"
                ErrorType.PAYMENT_FAILED -> "Payment transaction failed"
                ErrorType.INSUFFICIENT_FUNDS -> "Insufficient funds"
                ErrorType.INVALID_DETAILS -> "Invalid payment details"
                ErrorType.BANK_RESTRICTION -> "Bank transaction restricted"
            }
        )
    }
}

/**
 * Enum representing different types of payment errors
 */
enum class ErrorType {
    NETWORK_ERROR,
    PAYMENT_FAILED,
    INSUFFICIENT_FUNDS,
    INVALID_DETAILS,
    BANK_RESTRICTION
}