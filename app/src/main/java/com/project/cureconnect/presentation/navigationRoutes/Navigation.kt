package com.project.cureconnect.presentation.navigationRoutes


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.project.cureconnect.presentation.screens.DoctorPanel.NavigationRoutesDoctor.Navigation
import com.project.cureconnect.MainDashBoard
import com.project.cureconnect.presentation.screens.AuthScreen.AuthViewModel
import com.project.cureconnect.presentation.screens.AuthScreen.LoginScreen
import com.project.cureconnect.presentation.screens.AuthScreen.SignUpScreen
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser

import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import com.project.cureconnect.patients.cardScreens.consult.ChatScreen
import com.project.cureconnect.presentation.screens.AuthScreen.DoctorAuthViewModel
import com.project.cureconnect.presentation.screens.pateints.CardScreen.analysis.AnalysisScreen
import com.project.cureconnect.presentation.screens.pateints.CardScreen.analysis.MedicalAnalysisDashboard
import com.project.cureconnect.presentation.screens.pateints.CardScreen.analysis.Sampledataana.analyses
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.AppoinmenetViewModel
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.AppointmentConfirmationScreen
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.DoctorDetailsScreen
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.MyAppointmentsScreen
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.appoinmrnent

import com.project.cureconnect.presentation.screens.pateints.CardScreen.consult.consult
import com.project.cureconnect.presentation.screens.pateints.CardScreen.emergency.emergency
import com.project.cureconnect.presentation.screens.pateints.CardScreen.healthcrae.healthcre

import com.project.cureconnect.presentation.screens.pateints.chatbot.HealthChatBotScreen
import com.project.cureconnect.presentation.screens.pateints.Extrapages.EmergencyScreenUI
import com.project.cureconnect.presentation.screens.pateints.HistoryPage.PatientHistoryPage
import com.project.cureconnect.presentation.screens.pateints.ProfileScreen.ProfileScreen
import com.project.cureconnect.presentation.screens.pateints.Extrapages.Shop


import com.project.cureconnect.presentation.startpages.SplashScreen
import com.project.cureconnect.presentation.startpages.WelcomeScreenD1
import com.project.cureconnect.presentation.startpages.WelcomeScreenD2
import com.project.cureconnect.presentation.startpages.WelcomeScreenD3

import com.project.cureconnect.presentation.startpages.WelcomeScreenFirst
import com.project.cureconnect.presentation.startpages.WelcomeScreenSecond

import com.project.cureconnect.patients.cardScreens.telemedicines.telemedicineScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppHost(
    navController: NavHostController,
    startDestination: String
) {
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
    val appoinmenetViewModel : AppoinmenetViewModel = viewModel();


    val doctorViewModel: DoctorAuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DoctorAuthViewModel(sessionManager) as T
            }
        }
    )


    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.WelcomeScreen1.routes) {
            WelcomeScreenFirst(navController)
        }
        composable(Screen.WelcomeScreen2.routes) {
            WelcomeScreenSecond(navController)
        }
        composable(Screen.WelcomeScreen3.routes) {
            SignUpScreen(navController , authViewModel)
        }
        // Welcome Screens
        composable(Screen.WelcomeScreenD3.routes) {
            WelcomeScreenD3(navController , doctorViewModel)
        }
        composable(Screen.WelcomeScreenD2.routes) {
            WelcomeScreenD2(navController , doctorViewModel)
        }
        composable(Screen.WelcomeScreenD1.routes) {
            WelcomeScreenD1(navController, doctorViewModel)
        }

        // Authentication
        composable(Screen.SignUp.routes) {
            SignUpScreen(navController = navController , authViewModel)
        }
        composable(Screen.Login.routes) {
            LoginScreen(navController = navController , authViewModel)
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
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: return@composable
            val doctorFromCache = appoinmenetViewModel.getDoctorById(doctorId)

            if (doctorFromCache != null) {
                DoctorDetailsScreen(doctor = doctorFromCache, navController = navController , appoinmenetViewModel)
            } else {
                LaunchedEffect(Unit) {
                    appoinmenetViewModel.fetchDoctorByIdFromFirestore(doctorId) { doctor ->
                        doctor?.let {
                            navController.navigate("doctor_details/${it.uid}") // Refresh screen
                        }
                    }
                }
            }
        }

        composable("appointment_confirmation/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: return@composable
            val doctorFromCache = appoinmenetViewModel.getDoctorById(doctorId)

            if (doctorFromCache != null) {
                AppointmentConfirmationScreen(doctor = doctorFromCache, navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    appoinmenetViewModel.fetchDoctorByIdFromFirestore(doctorId) { doctor ->
                        doctor?.let {
                            navController.navigate("doctor_details/${it.uid}") // Refresh screen
                        }
                    }
                }
            }
        }
        composable("history") { backStackEntry ->
            PatientHistoryPage(navController)
        }


        composable(Screen.MyAppointment.routes) {
            MyAppointmentsScreen(navController , appoinmenetViewModel)
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
            val userId = cachedUser?.uid.orEmpty()

            // If you already have doctor data passed or stored elsewhere
            doctorId?.let {
                ChatScreen(
                    navController = navController,
                    doctorId = it,
                    userId = userId
                )
            } ?: run {
                // Optional fallback UI
                Text("Invalid doctor ID")
            }
        }

        composable(Screen.Consult.routes){
            consult(navController , appoinmenetViewModel)

        }

        composable(Screen.DoctorDashborad.routes) {
            Navigation()
        }












    }
}