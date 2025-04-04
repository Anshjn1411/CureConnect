package com.project.cureconnect.pateints.navigationRoutes

sealed class Screen(val routes : String){
    object SplashScreen : Screen("splash_screen")
    object WelcomeScreen : Screen("welcome_screen")
    object SignIn : Screen("sign_in")
    object SignUp : Screen("sign_up")
    object ForgotPassword : Screen("forgot_password")
    object MainDashBoard : Screen("main_dashboard")
    object Telemedicine : Screen("telemedicine")
    object Analysis : Screen("analysis")
    object HealthTips : Screen("health_tips")
    object Consult : Screen("consult")
    object Emergency : Screen("emergency")
    object Appointment : Screen("appointment")
    object Videobyutbe : Screen("videobyutbe")
    object home : Screen("home")
    object search : Screen("search")
    object profile : Screen("profile")
    object AvailabelDoctor : Screen("availble_doctor")
    object DoctorDashborad : Screen("doctor_dashborad")
    object chatScreen : Screen("chat_screen")
    object Navigation :Screen("navigation")



}

