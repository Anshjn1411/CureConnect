package com.project.cureconnect.presentation.screens.pateints.ProfileScreen
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.R

import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import com.project.cureconnect.presentation.navigationRoutes.Screen
import com.project.cureconnect.presentation.screens.pateints.MainScreen.MainBottomBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    var selectedItem by remember { mutableStateOf(3) }
    var cachedUser by remember { mutableStateOf<CachedUser?>(null) }

    // Observe cached user from DataStore
    LaunchedEffect(Unit) {
        sessionManager.userData.collect { user ->
            cachedUser = user
        }
        Log.d("ProfileScreen", "Cached user: $cachedUser")
        if (cachedUser == null) {
            navController.navigate(Screen.WelcomeScreen1.routes) {
                popUpTo(0)
            }
        }
    }


    Scaffold(
        bottomBar = { MainBottomBar(navController = navController , selectedItem = selectedItem){index->
            selectedItem =index

        } }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User profile image and name
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_logo),
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = cachedUser?.name ?: "Loading...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Health metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HealthMetric(
                    icon = R.drawable.ic_heart,
                    value = "215bpm",
                    label = "Heart rate",
                    tint = Color(0xFF4285F4)
                )

                HealthMetric(
                    icon = R.drawable.cureconnect_logo,
                    value = "756cal",
                    label = "Calories",
                    tint = Color(0xFF4285F4)
                )

                HealthMetric(
                    icon = R.drawable.cureconnect_logo,
                    value = "103lbs",
                    label = "Weight",
                    tint = Color(0xFF4285F4)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn {
                item {
                    ProfileMenuItem(
                        icon = R.drawable.cureconnect_logo,
                        title = "History",
                        onClick = { navController.navigate("history")
                             }
                    )
                }
                item {
                    ProfileMenuItem(
                        icon = R.drawable.cureconnect_logo,
                        title = "Appointment",
                        onClick = { navController.navigate(Screen.Appointment.routes) }
                    )

                }
                item {
                    ProfileMenuItem(
                        icon = R.drawable.cureconnect_logo,
                        title = "Payment Method",
                        onClick = { /* Navigate to payment methods */ }
                    )

                }
                item {
                    ProfileMenuItem(
                        icon = R.drawable.cureconnect_logo,
                        title = "FAQs",
                        onClick = { /* Navigate to FAQs */ }
                    )

                }
                item {
                    ProfileMenuItem(
                        icon = R.drawable.cureconnect_logo,
                        title = "Logout",
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                sessionManager.clearUser()
                            }
                                navController.navigate(Screen.WelcomeScreen1.routes){
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }


                        }
                    )
                }
            }

            // Menu items






        }
    }
}

@Composable
fun HealthMetric(
    icon: Int,
    value: String,
    label: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF1F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = Color(0xFF4285F4),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "More",
                tint = Color.Gray
            )
        }
    }
}