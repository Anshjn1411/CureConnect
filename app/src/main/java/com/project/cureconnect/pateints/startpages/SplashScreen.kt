package com.project.cureconnect.pateints.startpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.project.cureconnect.R
import com.project.cureconnect.login.AuthViewModel
import com.project.cureconnect.pateints.navigationRoutes.Screen
import com.project.cureconnect.ui.theme.comic

import kotlinx.coroutines.delay



@Composable
fun SplashScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val user by authViewModel.user
    val successLogin by authViewModel.successLogin

    LaunchedEffect(Unit) {
        delay(2000)

        if (successLogin && user != null) {
            navController.navigate(Screen.MainDashBoard.routes) {
                popUpTo(Screen.SplashScreen.routes) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.WelcomeScreen1.routes) {
                popUpTo(Screen.SplashScreen.routes) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.cureconnect_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(180.dp) // optional styling
        )
    }
}


