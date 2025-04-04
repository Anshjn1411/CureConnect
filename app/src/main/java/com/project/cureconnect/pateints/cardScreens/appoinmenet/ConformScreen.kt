package com.project.cureconnect.pateints.cardScreens.appoinmenet



import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentConfirmationScreen(navController: NavController) {
    val scrollState = rememberScrollState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appointment Confirmed", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Thank You!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your appointment has been confirmed", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF5F7FA))
                        ) {}

                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Dr. Smith", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text("Cardiologist", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    DetailItem(Icons.Default.CalendarMonth, "Date", "April 15, 2025")
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(Icons.Default.Schedule, "Time", "10:30 AM")
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(Icons.Default.Person, "Patient", "John Doe")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("my_appointments") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Text("View My Appointments", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Back to Home", fontSize = 16.sp, color = Color(0xFF4285F4))
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF4285F4), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, fontSize = 14.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}






//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppointmentConfirmationScreen(
//    appointmentId: String = "5260ad05-6b2a-4ab6-97c4-c4180c65e408",
//    doctorId: String = "3",
//    patientName: String = "John Doe",
//    navController: NavController
//) {
//    val scrollState = rememberScrollState()
//    var appointment by remember { mutableStateOf(Appointment()) }
//    var doctor by remember { mutableStateOf(Doctor()) }
//    var isLoading by remember { mutableStateOf(true) }
//
//
//    LaunchedEffect(appointmentId, doctorId) {
//        fetchAppointmentByID(appointmentId) { fetchedAppointment ->
//            appointment = fetchedAppointment!!
//            Log.d("checknuifn", fetchedAppointment.toString())
//            if (fetchedAppointment != null) isLoading = false
//        }
//        fetchDoctorByID(doctorId) { fetchedDoctor ->
//            doctor = fetchedDoctor!!
//            Log.d("checknuifn", fetchedDoctor.toString())
//            if (fetchedDoctor != null) isLoading = false
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Appointment Confirmed", fontSize = 18.sp, fontWeight = FontWeight.Medium) },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White
//                )
//            )
//        },
//        containerColor = Color.White
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .verticalScroll(scrollState)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Success Icon
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "Success",
//                    tint = Color(0xFF4CAF50),
//                    modifier = Modifier.size(40.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Thank you message
//            Text(
//                text = "Thank You!",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Your appointment has been confirmed",
//                fontSize = 16.sp,
//                color = Color.Gray,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // Appointment Details Card
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    // Doctor Info
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        // Doctor Image
//                        Box(
//                            modifier = Modifier
//                                .size(64.dp)
//                                .clip(RoundedCornerShape(8.dp))
//                                .background(Color(0xFFF5F7FA))
//                        ) {
//                            doctor.imageRes.let { painterResource(it) }?.let {
//                                Image(
//                                    painter = it,
//                                    contentDescription = "Doctor Image",
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.width(16.dp))
//
//                        // Doctor Details
//                        Column {
//                            Text(
//                                text = doctor.name,
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.SemiBold
//                            )
//
//                            Text(
//                                text = doctor.specialty,
//                                fontSize = 14.sp,
//                                color = Color.Gray
//                            )
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(24.dp))
//                    Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
//                    Spacer(modifier = Modifier.height(24.dp))
//
//                    // Appointment Details
//                    DetailItem(
//                        icon = Icons.Default.CalendarMonth,
//                        title = "Date",
//                        value = doctor.availableTimes.get(0)
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    DetailItem(
//                        icon = Icons.Default.Schedule,
//                        title = "Time",
//                        value = doctor.availableTimes.get(0)
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    DetailItem(
//                        icon = Icons.Default.Person,
//                        title = "Patient",
//                        value = patientName
//                    )
//
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Additional Information
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = "Additional Information",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        text = "• Please arrive 15 minutes before your appointment time",
//                        fontSize = 14.sp,
//                        color = Color.DarkGray
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = "• Bring your insurance card and ID",
//                        fontSize = 14.sp,
//                        color = Color.DarkGray
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = "• If you need to cancel, please do so 24 hours in advance",
//                        fontSize = 14.sp,
//                        color = Color.DarkGray
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Contact Us
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA)),
//                shape = RoundedCornerShape(16.dp)
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp)
//                ) {
//                    Text(
//                        text = "Have Questions?",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Email,
//                            contentDescription = "Email",
//                            tint = Color(0xFF4285F4),
//                            modifier = Modifier.size(18.dp)
//                        )
//
//                        Spacer(modifier = Modifier.width(8.dp))
//
//                        Text(
//                            text = "support@cureconnect.com",
//                            fontSize = 14.sp,
//                            color = Color(0xFF4285F4)
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Phone,
//                            contentDescription = "Phone",
//                            tint = Color(0xFF4285F4),
//                            modifier = Modifier.size(18.dp)
//                        )
//
//                        Spacer(modifier = Modifier.width(8.dp))
//
//                        Text(
//                            text = "+1 (555) 123-4567",
//                            fontSize = 14.sp,
//                            color = Color(0xFF4285F4)
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // View Appointments Button
//            Button(
//                onClick = { navController.navigate("my_appointments") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                shape = RoundedCornerShape(28.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF4285F4)
//                )
//            ) {
//                Text("View My Appointments", fontSize = 16.sp)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Home Button
//            Button(
//                onClick = { navController.navigate("main") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                shape = RoundedCornerShape(28.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.White
//                ),
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 0.dp
//                )
//            ) {
//                Text("Back to Home", fontSize = 16.sp, color = Color(0xFF4285F4))
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Text(
//                text = "Best Regards,\nThe CureConnect Team",
//                fontSize = 14.sp,
//                color = Color.Gray,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//        }
//    }
//}
//
//@Composable
//fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = title,
//            tint = Color(0xFF4285F4),
//            modifier = Modifier.size(22.dp)
//        )
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Column {
//            Text(
//                text = title,
//                fontSize = 14.sp,
//                color = Color.Gray
//            )
//
//            Text(
//                text = value,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color.Black
//            )
//        }
//    }
//}