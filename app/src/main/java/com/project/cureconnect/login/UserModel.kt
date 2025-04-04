package com.project.cureconnect.login

data class UserModel(
    val name: String = "",
    val email: String = "",
    val userId: String = "",
    val phone: String = "",
    val role : String = "",
    val loginMethod: String = "email", // Can be "email", "google", "phone", "facebook", "apple"
    val profilePicUrl: String = "",
    val isEmailVerified: Boolean = false,
    val medicalHistory : String? = null,
    val conditions : String? = null,
    val age : String? = null,
    val medications : String? = null,

)