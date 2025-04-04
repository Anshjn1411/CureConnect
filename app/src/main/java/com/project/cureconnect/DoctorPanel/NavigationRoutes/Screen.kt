package com.project.cureconnect.DoctorPanel.NavigationRoutes



sealed class Screen(val routes :String) {
    object DoctorDashBoard : Screen("DoctorDashBoard")
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

}