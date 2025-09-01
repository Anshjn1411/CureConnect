package com.project.cureconnect.presentation.startpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.project.cureconnect.R
import com.project.cureconnect.presentation.screens.AuthScreen.AuthViewModel
import com.project.cureconnect.presentation.navigationRoutes.Screen
import com.project.cureconnect.ui.theme.BackgroundWhite

import com.project.cureconnect.ui.theme.PrimaryBlue
import com.project.cureconnect.ui.theme.SecondaryTealDark

import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.user
    val doctor by authViewModel.doctor
    val successLogin by authViewModel.successLogin

    LaunchedEffect(Unit) {
        delay(2000)

        if (successLogin && user != null) {
            navController.navigate(Screen.MainDashBoard.routes) {
                popUpTo(Screen.SplashScreen.routes) { inclusive = true }
            }
        } else if (successLogin && doctor != null) {
            navController.navigate(Screen.DoctorDashborad.routes){
                popUpTo(Screen.SplashScreen.routes){ inclusive = true}
            }
        } else {
            navController.navigate(Screen.WelcomeScreen1.routes) {
                popUpTo(Screen.SplashScreen.routes) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

                Image(
                    painter = painterResource(id = R.drawable.cureconnect_logo),
                    contentDescription = "CureConnect Logo",
                    modifier = Modifier.size(80.dp)
                )

        }
    }
}

