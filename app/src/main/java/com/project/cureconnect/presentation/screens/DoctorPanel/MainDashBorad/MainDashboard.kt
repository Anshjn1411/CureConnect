package com.project.cureconnect.presentation.screens.DoctorPanel.MainDashBorad

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.presentation.screens.DoctorPanel.NavigationRoutesDoctor.Screen
import com.project.cureconnect.R
import com.project.cureconnect.ServiceItem

import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val primaryBlue = Color(0xFF4285F4)
val lightGrey = Color(0xFFF5F7FA)


@Composable
fun DoctorDashBoard(navController: NavController ) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(0) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        delay(300)
        isLoaded = true
    }

    Scaffold(
        topBar = {
            DoctorMainTopBar(navController)
        },
        bottomBar = {
            MainBottomBar(selectedItem = selectedItem, navController = navController) { index ->
                selectedItem = index
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Telemedicine.routes) },
                containerColor = primaryBlue
            ) {
                Icon(
                    imageVector = Icons.Default.VideoCall,
                    contentDescription = "Start Telemedicine Session",
                    tint = Color.White
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Wrap content in scrollable container
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Summary Cards
                item {
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                                slideInVertically(
                                    initialOffsetY = { -40 },
                                    animationSpec = tween(durationMillis = 500)
                                )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Dashboard",

                                fontWeight = FontWeight.Bold
                            )

                            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
                            Text(
                                text = currentDate,
                                color = Color.Gray,

                            )
                        }
                    }
                }

                // Services section
                item {
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = fadeIn(animationSpec = tween(durationMillis = 700)) +
                                slideInVertically(
                                    initialOffsetY = { -40 },
                                    animationSpec = tween(durationMillis = 700)
                                )
                    ) {
                        Text(
                            text = "Doctor Services",

                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }
                }

                item { DoctorServiceArea(navController) }

                // Upcoming appointments
                item {
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = fadeIn(animationSpec = tween(durationMillis = 900)) +
                                slideInVertically(
                                    initialOffsetY = { -40 },
                                    animationSpec = tween(durationMillis = 900)
                                )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Upcoming Appointments",

                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "View All",
                                    color = primaryBlue,
                                    modifier = Modifier.clickable {
                                        showDialog = true
                                    }
                                )
                            }
                            UnderDevelopmentDialog(
                                showDialog = showDialog,
                                onDismiss = { showDialog = false }
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }




                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DoctorServiceArea(navController: NavController) {
    val serviceItems = listOf(
        ServiceItem("Telemedicine", "🎥", R.drawable.banner_telemedicine, Screen.Telemedicine.routes),
        ServiceItem("Patient Records", "📋", R.drawable.banner_ai_analysis, "patient_records"),
        ServiceItem("Prescriptions", "💊", R.drawable.banner_health_tips, "prescriptions"),
        ServiceItem("Lab Results", "🔬", R.drawable.banner_instant_consultation, "lab_results"),
        ServiceItem("Consult", "✉️", R.drawable.banner_emergency, "consult"),
        ServiceItem("Calendar", "📅", R.drawable.banner_appointment, Screen.Appointment.routes)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServiceCard(serviceItems[0], navController, Modifier.weight(0.67f))
            ServiceCard(serviceItems[1], navController, Modifier.weight(0.67f))
            ServiceCard(serviceItems[2], navController, Modifier.weight(0.67f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ServiceCard(serviceItems[3], navController, Modifier.weight(0.67f))
            ServiceCard(serviceItems[4], navController, Modifier.weight(0.67f))
            ServiceCard(serviceItems[5], navController, Modifier.weight(0.67f))
        }
    }
}

@Composable
fun ServiceCard(service: ServiceItem, navController: NavController, modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing
        )
    )

    val infiniteTransition = rememberInfiniteTransition()
    val iconSize by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = modifier
            .scale(scale)
            .height(140.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                isPressed = true
                showDialog = true
                isPressed = false
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon with animation
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(lightGrey)
                    .scale(iconSize),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = service.emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = service.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            UnderDevelopmentDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false }
            )
        }
    }
}


@Composable
fun UnderDevelopmentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Feature in Development") },
            text = {
                Text("This panel is under development.\nPlease logout and login as a patient.")
            },
            confirmButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("OK")
                }
            }
        )
    }
}

