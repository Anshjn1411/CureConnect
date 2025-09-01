package com.project.cureconnect.presentation.startpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.cureconnect.R
import com.project.cureconnect.presentation.navigationRoutes.Screen
import com.project.cureconnect.ui.theme.BackgroundWhite
import com.project.cureconnect.ui.theme.DarkText
import com.project.cureconnect.ui.theme.PrimaryBlue


@Composable
fun WelcomeScreenSecond(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundWhite
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 🔹 Skip (top-right)
            Text(
                text = "Skip",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable {
                        navController.navigate(Screen.Login.routes)
                    },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 👩‍⚕️ Centered Doctor Image
                Image(
                    painter = painterResource(id = R.drawable.doctor_image), // Replace with your image
                    contentDescription = "Doctor Image",
                    modifier = Modifier
                        .height(500.dp)
                        .padding(bottom = 24.dp),
                    contentScale = ContentScale.Crop

                )

                // 📝 Main Title
                Text(
                    text = "Find a lot of specialist\ndoctors in one place",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                )
            }

            // 🔹 Bottom: Progress + Next Button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {


                // ➡️ Next Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue)
                        .clickable {
                            navController.navigate(Screen.WelcomeScreen3.routes)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

