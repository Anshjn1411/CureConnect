package com.project.cureconnect


import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.cureconnect.R
import com.project.cureconnect.login.UserModel
import com.project.cureconnect.pateints.mainScreens.BannerScreen
import com.project.cureconnect.pateints.mainScreens.HealthNewsSection
import com.project.cureconnect.pateints.mainScreens.HealthStatsSection
import com.project.cureconnect.pateints.mainScreens.MainBottomBar
import com.project.cureconnect.pateints.mainScreens.MainTopBar
import com.project.cureconnect.pateints.mainScreens.SpecialistDoctorsSection
import com.project.cureconnect.pateints.mainScreens.SpeechRecognitionHelper
import com.project.cureconnect.pateints.mainScreens.UserFeedbackSection
import com.project.cureconnect.pateints.navigationRoutes.Screen
import com.project.cureconnect.pateints.startpages.SplashScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import login.AuthViewModel


val primaryBlue = Color(0xFF4285F4)
val lightGrey = Color(0xFFF5F7FA)
val darkBlue = Color(0xFF3367D6)

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainDashBoard(navController: NavController , viewModel: AuthViewModel= viewModel()) {


    lateinit var speechHelper: SpeechRecognitionHelper
    var selectedItem by remember { mutableStateOf(0) }
    var isLoaded by remember { mutableStateOf(false) }
    speechHelper = SpeechRecognitionHelper(LocalContext.current , navController)
    LaunchedEffect(key1 = true) {
        delay(300)
        isLoaded = true
    }

    Scaffold(
        bottomBar = {
            MainBottomBar(selectedItem = selectedItem, navController = navController) { index ->
                selectedItem = index
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { speechHelper.startListening() },
                containerColor = if(speechHelper.isListening.value){
                    Color.Green
                }else{
                    primaryBlue
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "startVoice assist",
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
            viewModel.getUserData { user ->
                if (user != null) {
                    // Handle the user data
                    Log.d("UserData", "User Name: ${user.name}, Email: ${user.email}")
                } else {
                    // Handle the case when the user data is null
                    Log.d("UserData", "User not found or an error occurred")
                }
            }
            MainTopBar(navController ,speechHelper)

            // Wrap content in scrollable container
            LazyColumn {
                item { BannerScreen() }

                // Section title with animation
                item {
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                                slideInVertically(
                                    initialOffsetY = { -40 },
                                    animationSpec = tween(durationMillis = 500)
                                )
                    ) {
                        Text(
                            text = stringResource(R.string.our_service),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }
                }

                item { CardArea(navController) }

                // Specialist Doctors Section
                item {
                    SpecialistDoctorsSection(
                        visible = isLoaded,
                        navController = navController
                    )
                }

                // Health Stats Section
                item {
                    HealthStatsSection(
                        visible = isLoaded
                    )
                }

                // User Feedback Section
                item {
                    UserFeedbackSection(
                        visible = isLoaded
                    )
                }

                // Health News Section
                item {
                    HealthNewsSection(
                        visible = isLoaded,
                        navController = navController
                    )
                }

                // Bottom spacing
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}


@Composable
fun CardArea(navController: NavController) {
    val serviceItems = listOf(
        ServiceItem(stringResource(R.string.Telemedicine), "\uD83D\uDCDE", R.drawable.banner_telemedicine, Screen.Telemedicine.routes),
        ServiceItem(stringResource(R.string.Analysis), "\uD83D\uDCCB", R.drawable.banner_ai_analysis, Screen.Analysis.routes),
        ServiceItem(stringResource(R.string.Health_Tips), "😷", R.drawable.banner_health_tips, Screen.HealthTips.routes),
        ServiceItem(stringResource(R.string.Consult), "🩺", R.drawable.banner_instant_consultation, Screen.Consult.routes),
        ServiceItem(stringResource(R.string.Emergency), "🚑", R.drawable.banner_emergency, Screen.Emergency.routes),
        ServiceItem(stringResource(R.string.Appointment), "👨‍⚕️", R.drawable.banner_appointment, Screen.Appointment.routes)

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

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ServiceCard(service: ServiceItem, navController: NavController, modifier: Modifier = Modifier) {
    // Update the ServiceCard function to accept a modifier parameter
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
                // Navigate after a small delay to show the press animation
                navController.navigate(service.route)
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
        }
    }
}
// Data classes for organizing components
data class ServiceItem(
    val name: String,
    val emoji: String,
    val iconResId: Int,
    val route: String
)

data class BottomNavItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    val navController = rememberNavController()
    MainDashBoard(navController)
}