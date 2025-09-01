package com.project.cureconnect.presentation.screens.AuthScreen

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.Doctor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AuthViewModel(private val sessionManager: UserSessionManager) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user
    private val _doctor = mutableStateOf<Doctor?>(null)
    val doctor: State<Doctor?> = _doctor

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _successLogin = mutableStateOf(false)
    val successLogin: State<Boolean> = _successLogin

    private val _successSignup = mutableStateOf(false)
    val successSignup: State<Boolean> = _successSignup

    private val _loginMessage = mutableStateOf<String?>(null)
    val loginMessage: State<String?> = _loginMessage

    private val _signUpMessage = mutableStateOf<String?>(null)
    val signUpMessage: State<String?> = _signUpMessage

    init {
        autoLogin()
    }


    private fun autoLogin() {
        viewModelScope.launch {
            try {
                val cachedUser = sessionManager.userData.firstOrNull()

                if (cachedUser != null && cachedUser.name.isNotBlank() && cachedUser.email.isNotBlank()) {
                    if(cachedUser.role=="user"){
                        _user.value = User(
                            uid = cachedUser.uid,
                            name = cachedUser.name,
                            email = cachedUser.email,
                            phone = cachedUser.phone
                        )
                    }else{
                        _doctor.value = Doctor(
                            uid = cachedUser.uid,
                            name = cachedUser.name,
                            email = cachedUser.email,
                            phoneNumber = cachedUser.phone
                        )
                    }

                    _successLogin.value = true
                    Log.d("AutoLogin", "✅ Auto-login with user: ${cachedUser.name}")
                } else {
                    Log.d("AutoLogin", "❌ No valid session found")
                }

            } catch (e: Exception) {
                Log.e("AutoLogin", "❌ Exception in autoLogin: ${e.localizedMessage}")
            }
        }
    }


    fun login(email: String, password: String , isDoctor: Boolean = false, onLoginSuccess: (message :String) -> Unit) {
        _loading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val collection = if(isDoctor) "doctors" else "users"
                    firestore.collection(collection).document(uid).get()
                        .addOnSuccessListener { doc ->
                            if (isDoctor) {
                                val doctor = doc.toObject(Doctor::class.java)
                                if (doctor != null) {
                                    _doctor.value = doctor
                                    _successLogin.value = true
                                    _loginMessage.value = "Welcome Dr. ${doctor.name}!"
                                    viewModelScope.launch {
                                        sessionManager.saveDoctor(doctor)
                                    }
                                    onLoginSuccess("Success")
                                    Log.d("Login", "✅ Doctor login successful: ${doctor.name}")
                                } else {
                                    _loginMessage.value = "Doctor record not found."
                                }
                            } else {
                                val user = doc.toObject(User::class.java)
                                if (user != null) {
                                    _user.value = user
                                    _successLogin.value = true
                                    _loginMessage.value = "Welcome, ${user.name}!"
                                    viewModelScope.launch {
                                        sessionManager.saveUser(user)
                                    }
                                    onLoginSuccess("Success")
                                    Log.d("Login", "✅ User login successful: ${user.name}")
                                } else {
                                    _loginMessage.value = "User record not found."
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            _loginMessage.value = "Failed to load ${if (isDoctor) "doctor" else "user"} data."
                            onLoginSuccess("Error")
                            Log.e("Login", "❌ Firestore fetch error: ${e.localizedMessage}")
                        }
                } else {
                    _loginMessage.value = task.exception?.localizedMessage ?: "Login failed"
                    Log.e("Login", "❌ Firebase Auth error: ${task.exception?.localizedMessage}")
                }
            }
    }
    fun signup(
        name: String,
        email: String,
        password: String,
        phone: String,
        onSuccess: () -> Unit
    ) {
        _loading.value = true
        Log.d("Signup", "🔄 Starting signup process for: $email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        Log.e("Signup", "❌ Auth successful but UID is null")
                        _signUpMessage.value = "Authentication succeeded but UID is missing"
                        _successSignup.value = false
                        _loading.value = false
                        return@addOnCompleteListener
                    }

                    Log.d("Signup", "✅ Firebase Auth success: UID = $uid")

                    val newUser = User(
                        uid = uid,
                        name = name,
                        email = email,
                        phone = phone,
                        role = "user"
                    )

                    firestore.collection("users").document(uid).set(newUser)
                        .addOnSuccessListener {
                            Log.d("Signup", "✅ Firestore user added: $name")

                            _user.value = newUser
                            _successSignup.value = true
                            _signUpMessage.value = "Welcome, $name!"
                            viewModelScope.launch {
                                try {
                                    sessionManager.saveUser(newUser)
                                    onSuccess()
                                    Log.d("Signup", "✅ User saved in session manager.")
                                } catch (e: Exception) {
                                    Log.e("Signup", "❌ Error saving user in session manager: ${e.localizedMessage}")
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Signup", "❌ Firestore save error: ${e.localizedMessage}", e)
                            _signUpMessage.value = "Failed to save user data."
                            _successSignup.value = false
                        }

                } else {
                    val exception = task.exception
                    Log.e("Signup", "❌ Firebase Auth error: ${exception?.localizedMessage}", exception)

                    if (exception is FirebaseAuthException) {
                        Log.e("Signup", "❗ FirebaseAuthException code: ${exception.errorCode}")
                    }

                    _signUpMessage.value = exception?.localizedMessage ?: "Signup failed"
                    _successSignup.value = false
                }
                _loading.value = false
            }
            .addOnFailureListener { e ->
                Log.e("Signup", "❌ Firebase Auth failure (network/etc): ${e.localizedMessage}", e)
                _signUpMessage.value = "Network error during signup"
                _successSignup.value = false
                _loading.value = false
            }
    }


    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            auth.signOut()
            sessionManager.clearUser()
            delay(100)
            _user.value = null
            _successLogin.value = false
            Log.d("Logout", "🚪 User logged out")
            onLoggedOut()
        }
    }

    fun fetchDoctorsFromFirestore(onResult: (List<Doctor>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("doctors")
            .get()
            .addOnSuccessListener { result ->
                Log.d("DoctorFetch", "Doctors fetched successfully")
                Log.d("DoctorFetch" , result.documents.get(0).toString())

                val doctorsList = result.documents.mapNotNull { doc ->
                    doc.toObject(Doctor::class.java)?.copy(uid = doc.id)
                }

                onResult(doctorsList)
            }
            .addOnFailureListener { e ->
                Log.e("DoctorFetch", "Error fetching doctors", e)
                onResult(emptyList()) // Optionally return an empty list on failure
            }
    }

}




// UserModel.kt
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val gender: String = "",
    val role : String="user",
)