package com.project.cureconnect.pateints.cardScreens.appoinmenet

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.lang.reflect.Method


fun bookAppointment(appointment: Appointment) {
    val db = FirebaseFirestore.getInstance()

    val appointmentMap = hashMapOf(
        "id" to appointment.id,
        "doctorId" to appointment.doctorId,
        "patientId" to appointment.patientId,
        "date" to appointment.date,
        "time" to appointment.time,
        "status" to appointment.status,
        "patienHistory" to appointment.patientHistoryRecord
    )

    db.collection("appointments")
        .document(appointment.id)
        .set(appointmentMap)
        .addOnSuccessListener {
            Log.d("Appointment", "Appointment booked successfully!")
        }
        .addOnFailureListener { e ->
            Log.e("Appointment", "Failed to book appointment", e)
        }
}
