package com.project.cureconnect.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cureconnect.R
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val sessionManager: UserSessionManager) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

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
                    _user.value = User(
                        uid = cachedUser.uid,
                        name = cachedUser.name,
                        email = cachedUser.email,
                       phone = cachedUser.phone
                    )
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


    fun login(email: String, password: String) {
        _loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val user = doc.toObject(User::class.java)
                            if (user != null) {
                                _user.value = user
                                _successLogin.value = true
                                _loginMessage.value = "Welcome, ${user.name}!"
                                viewModelScope.launch {
                                    sessionManager.saveUser(user)
                                }
                                Log.d("Login", "✅ Login successful: ${user.name}")
                            }
                        }
                        .addOnFailureListener { e ->
                            _loginMessage.value = "Failed to load user data."
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
        phone: String
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
                        phone = phone
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
}




// UserModel.kt
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val age: String = "",
    val gender: String = ""
)