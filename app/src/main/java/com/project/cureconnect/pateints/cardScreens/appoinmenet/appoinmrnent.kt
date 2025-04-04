package com.project.cureconnect.pateints.cardScreens.appoinmenet

import android.util.Log
import androidx.compose.runtime.*

import androidx.compose.runtime.Composable

import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cureconnect.R
import com.project.cureconnect.login.UserModel



@Composable
fun appoinmrnent (navController: NavController){

    AvailableDoctorsScreen(navController)
}


fun fetchDoctorsFromFirestore(onResult: (List<Doctor>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("doctors").get()
        .addOnSuccessListener { result ->
            Log.d("Doctor", "doctors fetched successfully")
            val doctorsList = result.documents.mapNotNull { it.toObject(Doctor::class.java) }
            onResult(doctorsList)
        }
        .addOnFailureListener { e ->
            println("Error fetching doctors: ${e.message}")
        }
}
fun fetchAppointmentByIDFromFirestore(Id :String , onResult: (List<Appointment>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("appointments")
        .whereEqualTo("patientId", Id) // Fetch only appointments where patientId = 1
        .get()
        .addOnSuccessListener { result ->
            Log.d("Appointment", "Appointments fetched successfully")
            val appointmentList = result.documents.mapNotNull { it.toObject(Appointment::class.java) }
            onResult(appointmentList)
        }
        .addOnFailureListener { e ->
            Log.e("Appointment", "Error fetching appointments: ${e.message}")
        }
}
fun fetchLatestAppointment(userId: String, onResult: (Appointment?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("appointments")
        .whereEqualTo("patientId", userId)  // Fetch only for this user
        .orderBy("createdAt", Query.Direction.DESCENDING) // Order by latest first
        .limit(1) // Get only the latest appointment
        .get()
        .addOnSuccessListener { result ->
            val latestAppointment = result.documents.firstOrNull()?.toObject(Appointment::class.java)
            Log.d("Firestore", "Latest Appointment: $latestAppointment")
            onResult(latestAppointment)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error fetching latest appointment: ${e.message}")
            onResult(null)
        }
}
fun fetchAppointmentByID(appointmentId: String, onResult: (Appointment?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("appointments").document(appointmentId).get()
        .addOnSuccessListener { document ->
            val appointment = document.toObject(Appointment::class.java)
            onResult(appointment)
        }
        .addOnFailureListener {
            onResult(null)
        }
}

fun fetchDoctorByID(doctorId: String, onResult: (Doctor?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("doctors").document(doctorId).get()
        .addOnSuccessListener { document ->
            val doctor = document.toObject(Doctor::class.java)
            onResult(doctor)
        }
        .addOnFailureListener {
            onResult(null)
        }
}


