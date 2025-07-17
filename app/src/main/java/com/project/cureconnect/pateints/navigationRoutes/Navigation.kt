package com.project.cureconnect.pateints.navigationRoutes


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.api.ResourceDescriptor.History
import com.project.cureconnect.DoctorPanel.MainDashBorad.DoctorDashBoard
import com.project.cureconnect.DoctorPanel.MainDashBorad.appointments
import com.project.cureconnect.MainDashBoard
import com.project.cureconnect.login.AuthViewModel
import com.project.cureconnect.login.LoginScreen
import com.project.cureconnect.login.SignUpScreen
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser

import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import com.project.cureconnect.pateints.cardScreens.analysis.AnalysisScreen
import com.project.cureconnect.pateints.cardScreens.analysis.MedicalAnalysisDashboard
import com.project.cureconnect.pateints.cardScreens.analysis.Sampledataana.analyses
import com.project.cureconnect.pateints.cardScreens.appoinmenet.AppointmentConfirmationScreen
import com.project.cureconnect.pateints.cardScreens.appoinmenet.DoctorDetailsScreen
import com.project.cureconnect.pateints.cardScreens.appoinmenet.MyAppointmentsScreen
import com.project.cureconnect.pateints.cardScreens.appoinmenet.appoinmrnent
import com.project.cureconnect.pateints.cardScreens.appoinmenet.sampleDoctors


import com.project.cureconnect.pateints.cardScreens.consult.consult
import com.project.cureconnect.pateints.cardScreens.emergency.emergency
import com.project.cureconnect.pateints.cardScreens.healthcrae.HealthTip
import com.project.cureconnect.pateints.cardScreens.healthcrae.healthcre

import com.project.cureconnect.pateints.chatBot.HealthChatBotScreen
import com.project.cureconnect.pateints.mainScreens.EmergencyScreenUI
import com.project.cureconnect.pateints.mainScreens.PatientHistoryPage
import com.project.cureconnect.pateints.mainScreens.ProfileScreen
import com.project.cureconnect.pateints.mainScreens.Shop


import com.project.cureconnect.pateints.startpages.SplashScreen

import com.project.cureconnect.pateints.startpages.WelcomeScreenFirst
import com.project.cureconnect.pateints.startpages.WelcomeScreenSecond
import com.project.cureconnect.pateints.startpages.WelcomeScreenThird
import com.project.cureconnect.patients.cardScreens.consult.ChatScreen
import com.project.cureconnect.patients.cardScreens.telemedicines.telemedicineScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppHost(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    var cachedUser by remember { mutableStateOf<CachedUser?>(null) }

    // Observe cached user from DataStore
    LaunchedEffect(Unit) {
        sessionManager.userData.collect { user ->
            cachedUser = user
        }
    }

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(sessionManager) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = Screen.SplashScreen.routes) {

        // Splash
        composable(Screen.SplashScreen.routes) {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }

        // Welcome Screens
        composable(Screen.WelcomeScreen1.routes) {
            WelcomeScreenFirst(navController)
        }
        composable(Screen.WelcomeScreen2.routes) {
            WelcomeScreenSecond(navController)
        }
        composable(Screen.WelcomeScreen3.routes) {
            WelcomeScreenThird(navController)
        }

        // Authentication
        composable(Screen.SignUp.routes) {
            SignUpScreen(navController = navController)
        }
        composable(Screen.Login.routes) {
            LoginScreen(navController = navController)
        }

        // Dashboard & Main
        composable(Screen.MainDashBoard.routes) {
            MainDashBoard(navController)
        }
        composable(Screen.profile.routes){
            ProfileScreen(navController)
        }
        composable(Screen.Appointment.routes) {
            appoinmrnent(navController)
        }
        composable(Screen.Analysis.routes) {
            MedicalAnalysisDashboard(navController)
        }
        composable(
            route = "analysis/{analysisId}"
        ) { backStackEntry ->
            val analysisId = backStackEntry.arguments?.getString("analysisId") ?: ""
            val analysisItem = analyses.find { it.id == analysisId }
            analysisItem?.let { AnalysisScreen(navController = navController, analysisItem = it) }
        }

        composable("doctor_details/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            val doctor = sampleDoctors.sampleDoctors.find { it.id == doctorId }

            doctor?.let {
                DoctorDetailsScreen(doctor = it, navController = navController)
            }
        }
        composable("appointment_confirmation/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            val doctor = sampleDoctors.sampleDoctors.find { it.id == doctorId }

            doctor?.let {
                AppointmentConfirmationScreen(doctor = it, navController = navController)
            }
        }
        composable("history") { backStackEntry ->
            PatientHistoryPage(navController)
        }


        composable(Screen.MyAppointment.routes) {
            MyAppointmentsScreen(navController)
        }
        composable(Screen.search.routes) {
            HealthChatBotScreen(navController)
        }
        composable(Screen.Telemedicine.routes) {
            telemedicineScreen(navController)
        }
        composable("IVR") {
            EmergencyScreenUI(navController)

        }
        composable(Screen.HealthTips.routes) {
            healthcre(navController)
        }

        composable(Screen.Emergency.routes){
            emergency(navController)
        }
        composable("Shop") {
            Shop()
        }






        composable("chat_screen/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            val doctor = sampleDoctors.sampleDoctors.find { it.id.toString() == doctorId }

            if (doctor != null) {
                ChatScreen(navController= navController,doctorId = doctor.id , userId = cachedUser?.uid.toString())
            } else {
                Text("Doctor not found")
            }
        }
        composable(Screen.Consult.routes){
            consult(navController)

        }













    }
}