package com.project.cureconnect.presentation.screens.AuthScreen


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.Doctor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DoctorAuthViewModel(private val sessionManager: UserSessionManager) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor: State<Doctor?> = _doctor

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _successSignup = mutableStateOf(false)
    val successSignup: State<Boolean> = _successSignup

    private val _signupMessage = mutableStateOf<String?>(null)
    val signupMessage: State<String?> = _signupMessage


    fun updateDoctor(updatedDoctor: Doctor) {
        _doctorData.value = updatedDoctor
    }
    private val _doctorData = mutableStateOf(Doctor())
    val doctorData: State<Doctor> = _doctorData

    fun signupDoctor(
        doctor: Doctor,
        password: String,
        onSignupSuccess: (message :String) -> Unit,
    ) {
        _loading.value = true
        Log.d("DoctorSignup", "🔄 Starting signup for doctor: ${doctor.email}")

        auth.createUserWithEmailAndPassword(doctor.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        _signupMessage.value = "Authentication succeeded but UID is missing"
                        _successSignup.value = false
                        _loading.value = false
                        return@addOnCompleteListener
                    }

                    val newDoctor = Doctor(
                        uid = uid,
                        name = doctor.name,
                        email = doctor.email,
                        phoneNumber = doctor.phoneNumber,
                        specialty = doctor.specialty,
                        bio = doctor.bio,
                        upiId = doctor.upiId,
                        consultationFee = doctor.consultationFee,
                        availableTimes = doctor.availableTimes,
                        imageUrl = doctor.imageUrl,
                        rating = doctor.rating,
                        distance = doctor.distance,
                        ans = doctor.ans,
                        imageRes = doctor.imageRes
                    )

                    firestore.collection("doctors").document(uid).set(newDoctor)
                        .addOnSuccessListener {
                            _doctor.value = newDoctor
                            _successSignup.value = true
                            _signupMessage.value = "Welcome, Dr. ${doctor.name}!"
                            viewModelScope.launch {
                                try {
                                    sessionManager.saveDoctor(newDoctor)
                                    onSignupSuccess("Success")
                                } catch (e: Exception) {
                                    Log.e("DoctorSignup", "❌ Failed to save doctor in session: ${e.localizedMessage}")
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("DoctorSignup", "❌ Firestore save failed: ${e.localizedMessage}", e)
                            _signupMessage.value = "Failed to save doctor data."
                            _successSignup.value = false
                            onSignupSuccess("Error")
                        }

                } else {
                    val exception = task.exception
                    _signupMessage.value = exception?.localizedMessage ?: "Signup failed"
                    _successSignup.value = false
                    Log.e("DoctorSignup", "❌ Firebase Auth error: ${exception?.localizedMessage}", exception)
                }
                _loading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("DoctorSignup", "❌ Network error during signup: ${e.localizedMessage}", e)
                _signupMessage.value = "Network error during signup"
                _successSignup.value = false
                _loading.value = false
            }
    }

    fun logoutDoctor(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            auth.signOut()
            sessionManager.clearDoctor()
            delay(100)
            _doctor.value = null
            _successSignup.value = false
            Log.d("DoctorLogout", "🚪 Doctor logged out")
            onLoggedOut()
        }
    }
}
