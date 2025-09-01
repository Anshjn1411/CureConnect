package com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet
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
import com.project.cureconnect.data.model.Constant.emailSMPT
import kotlinx.coroutines.flow.MutableStateFlow

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport



import java.util.Properties
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import com.project.cureconnect.presentation.screens.AuthScreen.User
import com.project.cureconnect.presentation.screens.pateints.HistoryPage.PatientHistoryRecord
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

open class AppoinmenetViewModel : ViewModel() {

    private val _cachedDoctors = mutableStateListOf<Doctor>()
    val cachedDoctors: List<Doctor> get() = _cachedDoctors

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors

    private val _userData = MutableStateFlow<CachedUser?>(null)
    val userData: StateFlow<CachedUser?> = _userData

    private val _history = MutableStateFlow<List<PatientHistoryRecord>>(emptyList())
    val history: StateFlow<List<PatientHistoryRecord>> = _history

    private val _state = MutableStateFlow(MyAppointmentState())
    val state: StateFlow<MyAppointmentState> = _state.asStateFlow()


    fun loadAppointments(userId: String) {
        Log.d("AppointmentFetch", "Started fetching appointments for userId: $userId")

        fetchAppointmentsByUserId(
            userId = userId,
            onResult = { appointments ->
                Log.d("AppointmentFetch", "Fetched ${appointments.size} appointments for userId: $userId")

                _state.value = _state.value.copy(
                    appointments = appointments,
                    isLoading = false
                )

                // Now fetch doctor info
                fetchDoctorsFromFirestore { doctors ->
                    val map = doctors.associateBy { it.uid }
                    Log.d("AppointmentFetch", "Doctor map created with ${map.size} entries")
                    _state.value = _state.value.copy(doctorMap = map)
                }
            },
            onError = { error ->
                Log.e("AppointmentFetch", "Error fetching appointments for userId: $userId", error)
                _state.value = _state.value.copy(isLoading = false)
            }
        )
    }




    suspend fun fetchAppointmentsByUserId(
        userId: String,
        onError: (Exception) -> Unit
    ): List<Appointment> = suspendCancellableCoroutine { cont ->
        val db = FirebaseFirestore.getInstance()
        Log.d("Firestore", "Fetching appointments for userId: $userId")

        db.collection("appointments")
            .whereEqualTo("patientId", userId)
            .get()
            .addOnSuccessListener { result ->
                val appointments = result.documents.mapNotNull { it.toObject(Appointment::class.java) }
                Log.d("Firestore", "Fetched ${appointments.size} appointments")
                cont.resume(appointments, null)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to fetch appointments", e)
                onError(e)
                cont.resume(emptyList(), null)
            }
    }

    fun loadUserUser(sessionManager: UserSessionManager) {
        Log.d("AppointmentViewModel", "Collecting user data")
        viewModelScope.launch {
            sessionManager.userData.collect { user ->
                Log.d("AppointmentViewModel", "User data: $user")
                _userData.value = user
            }
        }
    }

    fun loadUserAndHistory(sessionManager: UserSessionManager) {
        Log.d("AppointmentViewModel", "Loading user and history")
        viewModelScope.launch {
            sessionManager.userData.collect { user ->
                _userData.value = user
                Log.d("AppointmentViewModel", "User: $user")
                user?.name?.let { fetchPatientHistory(it) }
            }
        }
    }

    private fun fetchPatientHistory(userName: String) {
        Log.d("Firestore", "Fetching history for user: $userName")
        Firebase.firestore.collection("patients")
            .document(userName)
            .collection("history")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("Firestore", "Fetched ${documents.size()} history records")
                _history.value = documents.map { doc ->
                    PatientHistoryRecord(
                        recordId = doc.id,
                        imageUrl = doc.getString("imageUrl") ?: "",
                        response = doc.getString("response") ?: "No response"
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching history", e)
            }
    }

    fun cancelAppointment(appointment: Appointment, onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("appointments").document(appointment.id)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Appointment canceled successfully.")

                // Remove the canceled appointment from state
                val updatedList = _state.value.appointments.filterNot { it.id == appointment.id }
                _state.value = _state.value.copy(appointments = updatedList)

                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error canceling appointment: ${e.message}")
                onComplete(false)
            }
    }


    fun generateRoomId(): String {
        val id = UUID.randomUUID().toString().replace("-", "").substring(0, 10)
        Log.d("AppointmentViewModel", "Generated room ID: $id")
        return id
    }

    fun successfullBooking(user: String?, appointment: Appointment, doctor: Doctor, name: String) {
        val roomID = generateRoomId()
        Log.d("AppointmentViewModel", "Booking success for $name, Email: $user, Room: $roomID")

        sendCustomEmail(
            email = user.toString(),
            subject = "Appointment Confirmation with ${doctor.name}",
            body = """
                Dear $name,

                Your appointment has been successfully scheduled with ${doctor.name}.

                Appointment Details:
                -------------------
                Date: ${appointment.date}
                Time: ${appointment.time}
                Doctor: ${doctor.name}
                Speciality: ${doctor.specialty}
                Room ID: ${roomID}

                Please arrive 10 minutes before your scheduled time.
                Regards,
                CureConnect
            """.trimIndent()
        )
    }

    fun fetchDoctorsFromFirestore(onResult: (List<Doctor>) -> Unit) {
        Log.d("DoctorFetch", "Fetching doctors from Firestore")
        val db = FirebaseFirestore.getInstance()
        db.collection("doctors")
            .get()
            .addOnSuccessListener { result ->
                Log.d("DoctorFetch", "Doctors fetched: ${result.size()}")
                val doctorsList = result.documents.mapNotNull { doc ->
                    doc.toObject(Doctor::class.java)?.copy(uid = doc.id)
                }
                _cachedDoctors.clear()
                _cachedDoctors.addAll(doctorsList)
                onResult(doctorsList)
            }
            .addOnFailureListener { e ->
                Log.e("DoctorFetch", "Error fetching doctors", e)
                onResult(emptyList())
            }
    }

    fun getDoctorById(id: String): Doctor? {
        Log.d("DoctorFetch", "Searching doctor in cache with id: $id")
        return _cachedDoctors.find { it.uid == id }
    }

    fun fetchDoctorIfNeeded(doctorId: String) {
        if (_state.value.doctorMap.containsKey(doctorId)) {
            Log.d("DoctorFetch", "Doctor $doctorId already present in state map.")
            return
        }

        fetchDoctorByIdFromFirestore(doctorId) { doctor ->
            if (doctor != null) {
                val updatedMap = _state.value.doctorMap.toMutableMap()
                updatedMap[doctorId] = doctor
                _state.value = _state.value.copy(doctorMap = updatedMap)

                Log.d("DoctorFetch", "Doctor $doctorId fetched and added to doctorMap")
            } else {
                Log.e("DoctorFetch", "Doctor $doctorId fetch returned null")
            }
        }
    }


    fun fetchDoctorByIdFromFirestore(doctorId: String, onResult: (Doctor?) -> Unit) {
        Log.d("DoctorFetch", "Fetching doctor by ID: $doctorId")
        Firebase.firestore.collection("doctors")
            .document(doctorId)
            .get()
            .addOnSuccessListener { document ->
                val doctor = document.toObject(Doctor::class.java)
                doctor?.let {
                    if (_cachedDoctors.none { it.uid == doctor.uid }) {
                        _cachedDoctors.add(doctor)
                        Log.d("DoctorFetch", "Doctor added to cache: ${doctor.uid}")
                    }
                }
                onResult(doctor)
            }
            .addOnFailureListener { e ->
                Log.e("DoctorFetch", "Error fetching doctor by ID", e)
                onResult(null)
            }
    }

    fun sendCustomEmail(email: String, subject: String, body: String) {
        Log.d("Email", "Sending email to $email")
        Thread {
            try {
                val mailSender = MailSender(emailSMPT.email, emailSMPT.password)
                mailSender.sendEmail(email, subject, body)
                Log.d("Email", "Email sent to $email")
            } catch (e: Exception) {
                Log.e("MailSender", "Error sending email", e)
            }
        }.start()
    }

    fun fetchAppointmentsByUserId(
        userId: String,
        onResult: (List<Appointment>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        Log.d("AppointmentFetch", "Querying Firestore for appointments with patientId = $userId")

        db.collection("appointments")
            .whereEqualTo("patientId", userId)
            .get()
            .addOnSuccessListener { result ->
                Log.d("AppointmentFetch", "Firestore query success: ${result.size()} documents")
                val appointments = result.documents.mapNotNull { it.toObject(Appointment::class.java) }
                Log.d("AppointmentFetch", "Mapped ${appointments.size} appointments from documents")
                onResult(appointments)
            }
            .addOnFailureListener { e ->
                Log.e("AppointmentFetch", "Firestore query failed for userId: $userId", e)
                onError(e)
            }
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


data class MyAppointmentState(
    val appointments: List<Appointment> = emptyList(),
    val doctorMap: Map<String, Doctor> = emptyMap(),
    val isLoading: Boolean = true
)