package com.project.cureconnect.pateints.cardScreens.appoinmenet

import com.project.cureconnect.pateints.mainScreens.PatientHistoryRecord


data class Doctor(
    val id: String = "",
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
    val consultationFee: Double = 0.0

)

data class Appointment(
    val id: String = "",
    val doctorId: String = "",
    val doctorname: String ="",
    val patientId: String = "", // Default value added
    val date: String= "",
    val time: String= "",
    var status: String= "" ,
    val patientHistoryRecord: PatientHistoryRecord? = null// "Scheduled", "Completed", "Cancelled"
)