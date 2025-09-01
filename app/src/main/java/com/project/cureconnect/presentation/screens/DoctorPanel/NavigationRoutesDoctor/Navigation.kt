package com.project.cureconnect.presentation.screens.DoctorPanel.NavigationRoutesDoctor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.project.cureconnect.DoctorPanel.MainDashBorad.ProfileScreen
import com.project.cureconnect.patients.cardScreens.telemedicines.telemedicineScreen
import com.project.cureconnect.presentation.navigationRoutes.AppHost
import com.project.cureconnect.presentation.screens.DoctorPanel.MainDashBorad.DoctorDashBoard
import com.project.cureconnect.presentation.screens.pateints.chatbot.HealthChatBotScreen


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    ) {


    NavHost(navController = navController, startDestination = Screen.DoctorDashBoard.routes) {
        composable(Screen.DoctorDashBoard.routes){
            DoctorDashBoard(navController)
        }

        composable(Screen.Telemedicine.routes){
            telemedicineScreen(navController)
        }
        composable(Screen.profile.routes){
            ProfileScreen(navController)
        }
        composable(Screen.search.routes){
            HealthChatBotScreen(navController)
        }

        composable("prescriptions"){
            DoctorDashBoard(navController)
        }
        composable("Exit"){
            AppHost(navController , startDestination = Screen.WelcomeScreenFirst.routes)
        }


    }
}