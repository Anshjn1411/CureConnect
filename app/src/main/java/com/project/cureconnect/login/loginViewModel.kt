package login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cureconnect.R

import com.project.cureconnect.login.UserModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    // Add a state to track authentication status
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val firestore = Firebase.firestore
    private lateinit var googleSignInClient: GoogleSignInClient

    init {

        checkAuthState()
    }

    fun initGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleGoogleSignInResult(result: ActivityResult, onResult: (Boolean, String?) -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)

            // Authenticate with Firebase using the Google ID token
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Check if user exists in Firestore
                            viewModelScope.launch {
                                try {
                                    val docSnapshot = firestore.collection("usersData")
                                        .document(user.uid)
                                        .get()
                                        .await()

                                    if (!docSnapshot.exists()) {
                                        // User doesn't exist in Firestore, create a new entry
                                        val name = user.displayName ?: ""
                                        val email = user.email ?: ""
                                        val phone = user.phoneNumber ?: ""

                                        val userModel = UserModel(name, email, user.uid, phone)

                                        firestore.collection("usersData")
                                            .document(user.uid)
                                            .set(userModel)
                                            .await()
                                    }

                                    _userEmail.value = user.email
                                    _isAuthenticated.value = true
                                    onResult(true, null)
                                } catch (e: Exception) {
                                    onResult(false, e.localizedMessage)
                                }
                            }
                        } else {
                            onResult(false, "User is null")
                        }
                    } else {
                        onResult(false, authTask.exception?.localizedMessage)
                    }
                }
        } catch (e: ApiException) {
            onResult(false, "Google sign in failed: ${e.localizedMessage}")
        }
    }

    private fun checkAuthState() {
        auth.currentUser?.let { user ->
            _userEmail.value = user.email
            _isAuthenticated.value = true
        }
    }

    fun login(userInput: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // First, check if input is an email
        if (userInput.contains("@")) {
            signInWithEmail(userInput, password, onResult)
        } else {
            // If not an email, check if it's a username or phone number
            findUserByUsernameOrPhone(userInput) { email ->
                if (email != null) {
                    signInWithEmail(email, password, onResult)
                } else {
                    onResult(false, "User not found with this username or phone number")
                }
            }
        }
    }

    private fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModelScope.launch {
                        _userEmail.value = auth.currentUser?.email
                        _isAuthenticated.value = true
                        getCurrentUserEmail()
                    }
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.localizedMessage)
                }
            }
    }

    private fun findUserByUsernameOrPhone(userInput: String, onResult: (String?) -> Unit) {
        // If it's a phone number (only digits)
        if (userInput.all { it.isDigit() }) {
            firestore.collection("usersData")
                .whereEqualTo("phone", userInput)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.documents[0].toObject(UserModel::class.java)
                        onResult(user?.email)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } else {
            // If it's a username
            firestore.collection("usersData")
                .whereEqualTo("name", userInput)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val user = documents.documents[0].toObject(UserModel::class.java)
                        onResult(user?.email)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null)
                }
        }
    }

    fun signup(email: String, password: String, name: String, phone: String,selectedRole : String, onResult: (Boolean, String?) -> Unit, ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val userId = authTask.result?.user?.uid
                    if (userId != null) {
                        val userModel = UserModel(name, email, userId, phone , selectedRole)

                        firestore.collection("usersData").document(userId)
                            .set(userModel)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    _isAuthenticated.value = true
                                    onResult(true, null)
                                } else {
                                    onResult(false, dbTask.exception?.localizedMessage ?: "Database Error")
                                }
                            }
                    } else {
                        onResult(false, "User ID is null")
                    }
                } else {
                    onResult(false, authTask.exception?.localizedMessage)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _isAuthenticated.value = false
        _userEmail.value = null
    }

    fun getCurrentUserEmail(): String? {
        viewModelScope.launch {
            userEmail.collectLatest { email ->
                Log.d("Email", "Fetched Email: $email")
            }
        }
        return auth.currentUser?.email
    }

    fun getUserData(onResult: (UserModel?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("usersData")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(UserModel::class.java)
                        Log.d("userdata1", "User Data: $user")
                        onResult(user)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } else {
            onResult(null)
        }
    }

}