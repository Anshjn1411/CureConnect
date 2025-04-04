package com.project.cureconnect.DoctorPanel.NavigationRoutes

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import com.project.cureconnect.DoctorPanel.MainDashBorad.DoctorAppointmentsScreen
import com.project.cureconnect.DoctorPanel.MainDashBorad.DoctorDashBoard
import com.project.cureconnect.DoctorPanel.MainDashBorad.ProfileScreen
import com.project.cureconnect.DoctorPanel.cardScreen.telemedicines

import com.project.cureconnect.DoctorPanel.cardScreen.consut.ChatScreen
import com.project.cureconnect.pateints.startpages.Startpage


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    ) {


    NavHost(navController = navController, startDestination = Screen.DoctorDashBoard.routes) {
        composable(Screen.DoctorDashBoard.routes){
            DoctorDashBoard(navController)
        }

        composable("doctor_appoinmenet/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            doctorId?.let {
                    DoctorAppointmentsScreen(navController,doctorId)
            }
        }
        composable(Screen.Telemedicine.routes){
            telemedicines(navController)
        }
        composable(Screen.profile.routes){
            ProfileScreen(navController)
        }
        composable("prescriptions"){
            DoctorDashBoard(navController)
        }
        composable("consult"){
            ChatScreen(navController)
        }
        composable("welcome_screen"){
            com.project.cureconnect.pateints.navigationRoutes.Navigation()
        }


    }
}