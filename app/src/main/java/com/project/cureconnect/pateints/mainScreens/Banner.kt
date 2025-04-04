package com.project.cureconnect.pateints.mainScreens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.cureconnect.R
import com.project.cureconnect.darkBlue
import com.project.cureconnect.primaryBlue

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



@Composable
fun BannerScreen() {
    val banners = listOf(
        BannerData(
            title = "👩‍⚕️ Telemedicine - Consult Instantly",
            description = "Video call with certified doctors even in low-network areas. Hassle-free and secure!",
            imageRes = R.drawable.banner_telemedicine,
            actionText = "Start Consultation 🚀",
            backgroundColor = Color(0xFFE3F2FD) // Blue shade for trust
        ),
        BannerData(
            title = "🤖 AI Health Analysis",
            description = "Upload X-rays, ECGs, or skin images. Get AI-powered reports with 95% accuracy in seconds!",
            imageRes = R.drawable.banner_ai_analysis,
            actionText = "Analyze Now 🔍",
            backgroundColor = Color(0xFFC8E6C9) // Green for reliability
        ),
        BannerData(
            title = "💡 Daily Health Tips",
            description = "Stay healthy with expert-backed daily wellness tips. Personalized for you!",
            imageRes = R.drawable.banner_health_tips,
            actionText = "Explore Tips 🩺",
            backgroundColor = Color(0xFFFFF9C4) // Orange for vibrancy
        ),
        BannerData(
            title = "📅 Easy Appointment Booking",
            description = "Book doctor appointments in a few taps. Get reminders & video call links instantly!",
            imageRes = R.drawable.banner_appointment,
            actionText = "Book Now 📌",
            backgroundColor = Color(0xFFFFCCBC) // Red for urgency
        ),
        BannerData(
            title = "🚨 Emergency Assistance",
            description = "Find hospitals near you, contact emergency services, or book an ambulance instantly!",
            imageRes = R.drawable.banner_emergency,
            actionText = "Get Help ⚠️",
            backgroundColor = Color(0xFFF8BBD0) // Red for emergency alert
        ),
        BannerData(
            title = "🤝 24/7 AI Chatbot Support",
            description = "Ask health-related queries anytime! Our AI chatbot is ready to assist you.",
            imageRes = R.drawable.banner_chatbot,
            actionText = "Ask Now 💬",
            backgroundColor = Color(0xFFD1C4E9) // Purple for AI
        ),
        BannerData(
            title = "🧑‍⚕️ Instant Consultation (No Internet)",
            description = "Chat with doctors using low-bandwidth WebSocket-based communication. No data? No problem!",
            imageRes = R.drawable.banner_instant_consultation,
            actionText = "Consult Offline 🌐",
            backgroundColor = Color(0xFFFFE0B2) // Deep blue for innovation
        ),
        BannerData(
            title = "📍 Real-Time Hospital Locator",
            description = "Find the nearest hospital with live availability and contact details in seconds.",
            imageRes = R.drawable.banner_hospital_locator,
            actionText = "Locate Now 🏥",
            backgroundColor = Color(0xFFB3E5FC) // Green for assistance
        )
    )


    val pagerState = rememberPagerState(initialPage = 0) { banners.size }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            coroutineScope.launch(Dispatchers.Default) {
                withContext(Dispatchers.Main) {
                    val nextPage = (pagerState.currentPage + 1) % banners.size
                    pagerState.animateScrollToPage(nextPage, animationSpec = tween(durationMillis = 800))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            BannerCard(bannerData = banners[page], fontSize = 20.sp)
        }



        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                val color = if (pagerState.currentPage == index)
                    Color.White else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}



data class BannerData(
    val title: String,
    val description: String,
    val imageRes: Int,
    val actionText: String,
    val backgroundColor: Color
)

@Composable
fun BannerCard(
    bannerData: BannerData,
    fontSize: TextUnit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = bannerData.backgroundColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Left side - Image
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // Discount badge
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD966)),
                    contentAlignment = Alignment.Center
                ) {

                }

                Image(
                    painter = painterResource(id = bannerData.imageRes),
                    contentDescription = bannerData.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            // Right side - Content
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = bannerData.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSize * 1f),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = bannerData.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize * 0.6f),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp).width(15.dp))

                Button(
                    onClick = { /* Handle action click */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = bannerData.actionText,

                        )
                }
            }
        }
    }
}
/*


@Composable
fun BannerScreen() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    // Auto slide effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = when (page) {
                                    0 -> listOf(primaryBlue, darkBlue)
                                    1 -> listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                                    else -> listOf(Color(0xFF5C6BC0), Color(0xFF3949AB))
                                }
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (page) {
                                0 -> "Find Your Doctor"
                                1 -> "Book Appointments"
                                else -> "Health Tips & More"
                            },
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (page) {
                                0 -> "Connect with top specialists in your area"
                                1 -> "Easy scheduling for your healthcare needs"
                                else -> "Stay healthy with expert advice"
                            },
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Page indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}
*/
