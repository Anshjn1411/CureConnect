package com.project.cureconnect.pateints.cardScreens.analysis

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ExtraArea(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    val context = LocalContext.current


    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    // Header Section
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Theme Toggle
        IconButton(onClick = { navController.navigateUp() }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null
            )
        }
        Spacer(Modifier.width(50.dp))
        Text(
            text = "Model Analysis",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )


    }

    Spacer(modifier = Modifier.height(24.dp))

    // Accuracy KPI Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Model Accuracy",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor.copy(alpha = 0.7f)
                )

                Text(
                    text = "90%-95%",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = textColor
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Blue.copy(alpha = if (isDarkMode) 0.2f else 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Blue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

//    // Accuracy Graph Card
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(220.dp),
//        colors = CardDefaults.cardColors(containerColor = cardColor),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = RoundedCornerShape(16.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Accuracy Trends",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = textColor,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Box(
//                        modifier = Modifier
//                            .size(10.dp)
//                            .background(Color.Blue, shape = RoundedCornerShape(2.dp))
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(
//                        text = "Accuracy",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = textColor.copy(alpha = 0.7f)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Simplified accuracy chart
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF0F0F0)),
//                contentAlignment = Alignment.Center
//            ) {
//                AccuracyChart(isDarkMode = isDarkMode)
//            }
//        }
 //   }

}


@Composable
fun AccuracyChart(isDarkMode: Boolean) {
    val lineColor = if (isDarkMode) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f)

    Column(modifier = Modifier.fillMaxSize()) {
        // Graph area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Y-axis labels
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "100%",
                    style = MaterialTheme.typography.bodySmall,
                    color = lineColor.copy(alpha = 0.7f)
                )
                Text(
                    text = "95%",
                    style = MaterialTheme.typography.bodySmall,
                    color = lineColor.copy(alpha = 0.7f)
                )
                Text(
                    text = "90%",
                    style = MaterialTheme.typography.bodySmall,
                    color = lineColor.copy(alpha = 0.7f)
                )
            }

            // Actual chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Data points representing accuracy over time (95-98% range)
                val accuracyPoints = listOf(95.6f, 96.2f, 95.8f, 97.1f, 96.9f, 97.4f, 97.2f)

                accuracyPoints.forEachIndexed { index, accuracy ->
                    // Normalize to chart height (90-100% range)
                    val normalizedHeight = (accuracy - 90f) / 10f
                    val animatedHeight by animateFloatAsState(targetValue = normalizedHeight)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height((animatedHeight * 120).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Color.Blue.copy(alpha = if (isDarkMode) 0.7f else 0.5f))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${accuracy}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = lineColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul")

            months.forEach { month ->
                Text(
                    text = month,
                    style = MaterialTheme.typography.bodySmall,
                    color = lineColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}
