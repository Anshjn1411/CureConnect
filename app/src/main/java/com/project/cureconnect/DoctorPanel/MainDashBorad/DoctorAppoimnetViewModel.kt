package com.project.cureconnect.DoctorPanel.MainDashBorad

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Simplified Appointment class
data class Appointment(
    val id: String = "",
    val doctorId: String = "",
    val patientId: String = "",
    val date: String = "",
    val time: String = "",
    var status: String = "",
    val reason: String = "",
    val patientName: String = ""
)

// Enum for appointment status
enum class AppointmentStatus {
    SCHEDULED,
    CONFIRMED,
    PENDING,
    COMPLETED,
    CANCELLED,
    RESCHEDULED,
    NO_SHOW
}

class DoctorAppointmentViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val appointmentsCollection = firestore.collection("appointments")

    // Current doctor ID - should be set when the doctor logs in
    private val _currentDoctorId = mutableStateOf("")
    val currentDoctorId = _currentDoctorId

    // State for all appointments
    private val _appointments = MutableStateFlow<List<PatientAppointment>>(emptyList())
    val appointments: StateFlow<List<PatientAppointment>> = _appointments.asStateFlow()

    // Loading states
    private val _isLoading = mutableStateOf(false)
    val isLoading = _isLoading

    // Error handling
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun setDoctorId(doctorId: String) {
        _currentDoctorId.value = doctorId
        fetchAppointmentsForDoctor()
    }

    // Fetch appointments specifically for the logged-in doctor
    private fun fetchAppointmentsForDoctor() {
        Log.d("DoctorAppointments", "Fetching appointments for doctor: ${_currentDoctorId.value}")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (_currentDoctorId.value.isEmpty()) {
                    _errorMessage.value = "Doctor ID not set"
                    return@launch
                }

                val snapshot = appointmentsCollection
                    .whereEqualTo("doctorId", _currentDoctorId.value)
                    .get()
                    .await()

                Log.d("DoctorAppointments", "Found ${snapshot.documents.size} appointments")

                val appointmentsList = snapshot.documents.mapNotNull { document ->
                    try {
                        val appointment = document.toObject(Appointment::class.java)
                        appointment?.let {
                            // Fetch patient name from patients collection
                            val patientName = fetchPatientName(it.patientId) ?: "Unknown Patient"

                            // Convert to PatientAppointment
                            PatientAppointment(
                                id = it.id,
                                patientId = it.patientId,
                                patientName = patientName,
                                date = it.date,
                                time = it.time,
                                status = it.status,
                                reason = it.reason
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("DoctorAppointments", "Error parsing appointment: ${e.message}")
                        _errorMessage.value = "Error parsing appointment data: ${e.message}"
                        null
                    }
                }

                _appointments.value = appointmentsList
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("DoctorAppointments", "Error fetching appointments: ${e.message}")
                _errorMessage.value = "Error fetching appointments: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Helper function to fetch patient name
    private suspend fun fetchPatientName(patientId: String): String? {
        return try {
            val patientDoc = firestore.collection("patients").document(patientId).get().await()
            patientDoc.getString("name")
        } catch (e: Exception) {
            Log.e("DoctorAppointments", "Error fetching patient name: ${e.message}")
            null
        }
    }

    // Update appointment status
    fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Update in Firebase
                appointmentsCollection.document(appointmentId)
                    .update("status", newStatus.name.capitalize())
                    .await()

                // Update in local state
                val currentList = _appointments.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == appointmentId }
                if (index != -1) {
                    val appointment = currentList[index]
                    val updatedAppointment = appointment.copy(status = newStatus.name.capitalize())
                    currentList[index] = updatedAppointment
                    _appointments.value = currentList
                }

                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update appointment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Confirm appointment
    fun confirmAppointment(appointmentId: String) {
        updateAppointmentStatus(appointmentId, AppointmentStatus.CONFIRMED)
    }

    // Cancel appointment
    fun cancelAppointment(appointmentId: String) {
        updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED)
    }

    // Get appointments for a specific date
    fun getAppointmentsForDate(date: String): List<PatientAppointment> {
        return _appointments.value.filter { it.date == date }
    }

    // Get today's appointments
    fun getTodayAppointments(): List<PatientAppointment> {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        return getAppointmentsForDate(today)
    }

    // Manual refresh function to fetch latest appointments
    fun refreshAppointments() {
        fetchAppointmentsForDoctor()
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }

    // String extension to capitalize first letter of each word
    private fun String.capitalize(): String {
        return this.lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
    }
}

data class PatientAppointment(
    val id: String,
    val patientId: String,
    val patientName: String,
    val date: String,
    val time: String,
    val status: String,
    val reason: String = ""
)