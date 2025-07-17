package com.project.cureconnect.pateints.cardScreens.appoinmenet
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.project.cureconnect.pateints.Constant.emailSMPT

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport



import java.util.Properties

class AppoinmenetViewModel : ViewModel() {


    fun cancelAppointment(appointment: Appointment, onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointments").document(appointment.id)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Appointment canceled successfully.")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error canceling appointment: ${e.message}")
                onComplete(false)
            }
    }


    fun generateRoomId(): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10)
    }


    fun successfullBooking(user : String?, appointment: Appointment, doctor: Doctor, name :String){
        val roomID : String = generateRoomId()


        Log.d("qwe", user.toString())
        sendCustomEmail(
            email = user.toString(),
            subject = "Appointment Confirmation with ${doctor.name} ",
            body = "Dear $name,\n" +
                    "\n" +
                    "    Your appointment has been successfully scheduled with ${doctor.name}.\n" +
                    "\n" +
                    "    Appointment Details:\n" +
                    "    -------------------\n" +
                    "    Date: ${appointment.date}\n" +
                    "    Time: ${appointment.time}\n" +
                    "    Doctor: ${doctor.name}\n" +
                    "    Speciality: ${doctor.specialty}\n" +
                    "    Description: zftfgfxgdf\n" +
                    "    Room ID: ${roomID}\n" +
                    "\n" +
                    "    Please arrive 10 minutes before your scheduled appointment time.\n" +
                    "    If you need to reschedule or cancel your appointment, please do so at least 24 hours in advance.\n" +
                    "\n" +
                    "    Best regards,\n" +
                    "    CureConnect"
        )

    }

}


fun sendCustomEmail(email: String, subject: String, body: String) {
    Thread {
        try {
            val mailSender = MailSender(emailSMPT.email, emailSMPT.password)
            mailSender.sendEmail(email, subject, body)
        } catch (e: Exception) {
            Log.e("MailSender", "Error sending email: ${e.message}", e)
        }
    }.start()
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



class MailSender(private val user: String, private val pass: String) {
    fun sendEmail(to: String, subject: String, body: String) {
        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.port", "587")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(user, pass)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(user))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject)
                setText(body)
            }
            Transport.send(message)
            Log.d("MailSender", "Email Sent Successfully ✅")
        } catch (e: MessagingException) {
            e.printStackTrace()
            Log.e("MailSender", "Email Sending Failed: ${e.message} ❌")
        }
    }
}
