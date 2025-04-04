package com.project.cureconnect.pateints.navigationRoutes


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.api.ResourceDescriptor.History
import com.project.cureconnect.DoctorPanel.MainDashBorad.DoctorDashBoard
import com.project.cureconnect.DoctorPanel.MainDashBorad.appointments
import com.project.cureconnect.MainDashBoard

import com.project.cureconnect.pateints.cardScreens.analysis.Analysis
import com.project.cureconnect.pateints.cardScreens.analysis.MedicalAnalysisDashboard
import com.project.cureconnect.pateints.cardScreens.analysis.Sampledataana
import com.project.cureconnect.pateints.cardScreens.appoinmenet.Appointment
import com.project.cureconnect.pateints.cardScreens.appoinmenet.AppointmentConfirmationScreen
import com.project.cureconnect.pateints.cardScreens.appoinmenet.AvailableDoctorsScreen
import com.project.cureconnect.pateints.cardScreens.appoinmenet.Doctor
import com.project.cureconnect.pateints.cardScreens.appoinmenet.DoctorDetailsScreen
import com.project.cureconnect.pateints.cardScreens.appoinmenet.MyAppointmentsScreen

import com.project.cureconnect.pateints.cardScreens.appoinmenet.appoinmrnent
import com.project.cureconnect.pateints.cardScreens.appoinmenet.fetchAppointmentByIDFromFirestore
import com.project.cureconnect.pateints.cardScreens.appoinmenet.fetchDoctorsFromFirestore
import com.project.cureconnect.pateints.cardScreens.appoinmenet.fetchLatestAppointment

import com.project.cureconnect.pateints.cardScreens.consult.ChatScreen
import com.project.cureconnect.pateints.cardScreens.consult.consult
import com.project.cureconnect.pateints.cardScreens.emergency.emergency
import com.project.cureconnect.pateints.cardScreens.healthcrae.healthcre

import com.project.cureconnect.pateints.cardScreens.telemedicines.telemedicines
import com.project.cureconnect.pateints.chatBot.HealthChatBotScreen
import com.project.cureconnect.pateints.chatBot.HealthChatBotWelcomeScreen
import com.project.cureconnect.pateints.mainScreens.EmergencyScreenUI

import com.project.cureconnect.pateints.mainScreens.PatientHistoryPage
import com.project.cureconnect.pateints.mainScreens.ProfileScreen
import com.project.cureconnect.pateints.mainScreens.Shop


import com.project.cureconnect.pateints.startpages.SplashScreen
import com.project.cureconnect.pateints.startpages.Startpage
import login.AuthViewModel
import login.ForgotPassword
import login.SignIn
import login.SignUp

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    var userId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }


// Fetch user details first
    LaunchedEffect(Unit) {
        authViewModel.getUserData { user ->
            userId = user?.userId ?: ""
            name = user?.name ?: ""
            number = user?.phone ?: ""

            Log.d("detail", "User ID: $userId, Name: $name, Number: $number")
            fetchDoctorsFromFirestore { fetchedDoctors ->
                doctors = fetchedDoctors
            }



        }
    }

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    LaunchedEffect(key1 = isAuthenticated) {
        if (isAuthenticated) {

            authViewModel.getUserData { user ->
                if (user?.role == "Doctor") {
                    navController.navigate(Screen.Navigation.routes) {
                        popUpTo(0) { inclusive = true }
                    }

                } else {
                    navController.navigate(Screen.MainDashBoard.routes) {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.SplashScreen.routes) {
        composable(Screen.SplashScreen.routes) {
            SplashScreen(navController) {
                // Navigate based on authentication status after splash
                if (isAuthenticated) {
                    authViewModel.getUserData { user ->
                        if (user?.role == "Doctor") {
                            navController.navigate(Screen.Navigation.routes) {
                                popUpTo(0) { inclusive = true }
                            }

                        } else {
                            navController.navigate(Screen.MainDashBoard.routes) {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    }
                } else {
                    navController.navigate(Screen.WelcomeScreen.routes)
                }
            }
        }

        composable(Screen.WelcomeScreen.routes) {
            Startpage(navController)
        }

        composable(Screen.MainDashBoard.routes) {
            MainDashBoard(navController)
        }

        composable(Screen.SignIn.routes) {
            SignIn(navController, onLoginSuccess = {
                authViewModel.getUserData { user ->
                    if (user?.role == "Doctor") {
                        navController.navigate(Screen.Navigation.routes) {
                            popUpTo(0) { inclusive = true }
                        }

                    } else {
                        navController.navigate(Screen.MainDashBoard.routes) {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }

                })
        }
        composable(Screen.Navigation.routes){
            com.project.cureconnect.DoctorPanel.NavigationRoutes.Navigation()
        }

        composable(Screen.SignUp.routes) {
            SignUp(navController)
        }

        composable(Screen.ForgotPassword.routes) {
            ForgotPassword(navController)
        }

        composable(Screen.Telemedicine.routes) {
            telemedicines(navController)
        }

        composable(Screen.Analysis.routes) {
            MedicalAnalysisDashboard(navController)
        }
        composable("ananyisis/{Id}") { backStackEntry->
            val Id = backStackEntry.arguments?.getString("Id")
            Id?.let {
                val anaysis = Sampledataana.analyses.find { doctor -> doctor.id == Id }
                anaysis?.let {
                    Analysis(navController , anaysis)
                }
            }

        }

        composable(Screen.HealthTips.routes) {
            Log.d("ansh", "Ansh")
            healthcre(navController)
        }

        composable(Screen.Consult.routes) {
            consult(navController)
        }

        composable(Screen.Emergency.routes) {
            emergency(navController)
        }

        composable(Screen.Appointment.routes) {
            appoinmrnent(navController)
        }
        composable(Screen.profile.routes) {
            ProfileScreen(navController)

        }
        composable(Screen.search.routes) {
            HealthChatBotWelcomeScreen(navController)

        }

        composable("health_chatbot") {
            HealthChatBotScreen(navController)

        }
        composable("available_doctors") {
            AvailableDoctorsScreen(navController)
        }
        composable("doctor_details/{doctorId}") { backStackEntry ->
            Log.d("checknuifn", "iwndoenoen")
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            doctorId?.let {
                val doctor = doctors.find { doctor -> doctor.id == doctorId }
                doctor?.let {
                    Log.d("checknuifn", doctor.name)
                    DoctorDetailsScreen(doctor = doctor, navController = navController)
                }
            }
        }
        composable("my_appointments") {
            MyAppointmentsScreen(navController)
        }
        composable("main") {
            MainDashBoard(navController)
        }
        composable("shop") {
            Shop()
        }
        composable("history/{doctorId}")
            { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId")
                doctorId?.let {
                        PatientHistoryPage( navController = navController , doctorId)

                }
            }
        composable("appointment_confirmation") {

            AppointmentConfirmationScreen(navController = navController)


        }
        composable("IVR") {
            EmergencyScreenUI(navController)
        }
        composable(Screen.DoctorDashborad.routes) {
            DoctorDashBoard(navController)
        }
        composable(Screen.chatScreen.routes) {
            ChatScreen(navController)
        }

    }
}