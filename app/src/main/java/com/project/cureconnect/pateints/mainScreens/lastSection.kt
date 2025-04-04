package com.project.cureconnect.pateints.mainScreens


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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.cureconnect.R
import com.project.cureconnect.darkBlue
import com.project.cureconnect.lightGrey
import com.project.cureconnect.pateints.navigationRoutes.Screen
import com.project.cureconnect.primaryBlue

@Composable
fun SpecialistDoctorsSection(visible: Boolean, navController: NavController) {
    val doctors = listOf(
        DoctorInfo(stringResource(R.string.Dr_Sarah_Wilson), stringResource(R.string.Cardiologist), 4.9f, R.drawable.banner_telemedicine),
        DoctorInfo(stringResource(R.string.Dr_James_Chen), stringResource(R.string.Neurologist), 4.8f, R.drawable.banner_telemedicine),
        DoctorInfo(stringResource(R.string.Dr_Maria_Garcia), stringResource(R.string.Pediatrician), 5.0f, R.drawable.banner_telemedicine),
        DoctorInfo(stringResource(R.string.Dr_Robert_Lee), stringResource(R.string.Dermatologist), 4.7f, R.drawable.banner_telemedicine)

    )

    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(durationMillis = 500, delayMillis = 300)) +
                    slideInVertically(
                        initialOffsetY = { 40 },
                        animationSpec = tween(durationMillis = 500, delayMillis = 300)
                    )
        ) {
            Column {
                SectionTitleWithViewAll(
                    title = stringResource(R.string.Our_pecialist_Doctors) ,
                    onViewAllClick = { navController.navigate(Screen.Appointment.routes) }
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(doctors) { doctor ->
                        DoctorCard(doctor = doctor, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: DoctorInfo, navController: NavController) {
    Card(
        modifier = Modifier
            .size(width = 160.dp, height = 250.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { navController.navigate(Screen.Appointment.routes) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Doctor image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(primaryBlue.copy(alpha = 0.7f), darkBlue)
                        )
                    )
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = doctor.imageRes),
                    contentDescription = "Doctor ${doctor.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 20.dp)
                )

                // Rating pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 12.sp)
                        Text(
                            text = doctor.rating.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = doctor.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Text(
                    text = doctor.specialty,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                androidx.compose.material3.Button(
                    onClick = { navController.navigate(Screen.Appointment.routes) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    ),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.Book_Now),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun HealthStatsSection(visible: Boolean) {
    val stats = listOf(
        HealthStat(stringResource(R.string.Heart_Rate), "72 bpm", 0.72f, primaryBlue),
        HealthStat(stringResource(R.string.Steps), "8,450", 0.85f, Color(0xFF4CAF50)),
        HealthStat(stringResource(R.string.Sleep), "7.5 hrs", 0.78f, Color(0xFF9C27B0)),
        HealthStat(stringResource(R.string.Water), "1.8/2.5L", 0.72f, Color(0xFF03A9F4))

    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 500, delayMillis = 400)) +
                slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(durationMillis = 500, delayMillis = 400)
                )
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                stringResource(R.string.Your_Health_Stats),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )

            // Fixed: Replace LazyVerticalGrid with a normal Grid implementation to avoid nested scrollable containers
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // First column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HealthStatCard(stats[0])
                    HealthStatCard(stats[2])
                }

                // Second column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HealthStatCard(stats[1])
                    HealthStatCard(stats[3])
                }
            }
        }
    }
}

// Flat list for news instead of nested column in LazyColumn
@Composable
fun HealthNewsSection(visible: Boolean, navController: NavController) {
    val news = listOf(
        HealthNews(
            title = stringResource(R.string.Heart_Disease_Prevention),
            source = stringResource(R.string.Health_Today),
            timeAgo = stringResource(R.string.Three_Hours_Ago),
            imageRes = R.drawable.banner_health_tips
        ),
        HealthNews(
            title = stringResource(R.string.Covid_Booster_Info),
            source = stringResource(R.string.Medical_Journal),
            timeAgo = stringResource(R.string.One_Day_Ago),
            imageRes = R.drawable.banner_health_tips
        ),
        HealthNews(
            title = stringResource(R.string.Diabetes_Breakthrough),
            source = stringResource(R.string.Science_Daily),
            timeAgo = stringResource(R.string.Two_Days_Ago),
            imageRes = R.drawable.banner_health_tips
        )

    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 500, delayMillis = 600)) +
                slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(durationMillis = 500, delayMillis = 600)
                )
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            SectionTitleWithViewAll(
                title = "Latest Health News",
                onViewAllClick = { navController.navigate(Screen.HealthTips.routes) }
            )

            // Display just the first news item to avoid too much content in LazyColumn
            NewsCard(news[0], navController)

            // "Show More" button instead of listing all news items
            androidx.compose.material3.Button(
                onClick = { navController.navigate(Screen.HealthTips.routes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = lightGrey,
                    contentColor = primaryBlue
                )
            ) {
                Text("See More Health News")
            }
        }
    }
}

@Composable
fun HealthStatCard(stat: HealthStat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stat.name,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Text(
                text = stat.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            LinearProgressIndicator(
                progress = stat.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = stat.color,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun UserFeedbackSection(visible: Boolean) {
    val feedback = listOf(
        UserFeedback(
            name = stringResource(R.string.Emily_Johnson),
            date = stringResource(R.string.Two_Days_Ago),
            comment = stringResource(R.string.Telemedicine_Feedback),
            rating = 5f
        ),
        UserFeedback(
            name = stringResource(R.string.Michael_Brown),
            date = stringResource(R.string.One_Week_Ago),
            comment = stringResource(R.string.Appointment_Feedback),
            rating = 4.5f
        ),
        UserFeedback(
            name = stringResource(R.string.Jessica_Lee),
            date = stringResource(R.string.Three_Days_Ago),
            comment = stringResource(R.string.Health_Tips_Feedback),
            rating = 5f
        )

    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 500, delayMillis = 500)) +
                slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(durationMillis = 500, delayMillis = 500)
                )
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                stringResource(R.string.What_Our_Users_Say),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(feedback) { feedbackItem ->
                    FeedbackCard(feedbackItem)
                }
            }
        }
    }
}

@Composable
fun FeedbackCard(feedback: UserFeedback) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User info and rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = feedback.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = feedback.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(lightGrey)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 12.sp)
                        Text(
                            text = feedback.rating.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Comment
            Text(
                text = "\"${feedback.comment}\"",
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun NewsCard(news: HealthNews, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { navController.navigate(Screen.HealthTips.routes) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // News image
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = news.imageRes),
                    contentDescription = news.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // News details
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = news.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = news.source,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = news.timeAgo,
                        fontSize = 12.sp,
                        color = primaryBlue
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitleWithViewAll(title: String, onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "View All",
            color = primaryBlue,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onViewAllClick() }
        )
    }
}

// Data classes for the new sections
data class DoctorInfo(
    val name: String,
    val specialty: String,
    val rating: Float,
    val imageRes: Int
)

data class HealthStat(
    val name: String,
    val value: String,
    val progress: Float,
    val color: Color
)

data class UserFeedback(
    val name: String,
    val date: String,
    val comment: String,
    val rating: Float
)

data class HealthNews(
    val title: String,
    val source: String,
    val timeAgo: String,
    val imageRes: Int
)