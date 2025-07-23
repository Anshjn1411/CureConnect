package com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet

import com.project.cureconnect.presentation.screens.pateints.HistoryPage.PatientHistoryRecord


data class Doctor(
    val uid: String = "",
    val name: String = "",
    val specialty: String = "",
    val imageUrl :String ="",
    val rating: Float = 0.0f,
    val bio: String = "",
    val email: String = "",
    val availableTimes: List<String> = emptyList(),
    val distance: String = "",
    val ans: Int = 0,
    val imageRes: Int = 0,
    val upiId: String = "",
    val consultationFee: Double = 0.0,
    val phoneNumber: String = "",
    val password: String = "",
    val role : String = "doctor"

)

data class Appointment(
    val id: String = "",
    val doctorId: String = "",
    val doctorname: String ="",
    val patientId: String = "", // Default value added
    val date: String= "",
    val time: String= "",
    var status: String= "" ,
    val patientHistoryRecord: List<PatientHistoryRecord?> = emptyList()// "Scheduled", "Completed", "Cancelled"
)