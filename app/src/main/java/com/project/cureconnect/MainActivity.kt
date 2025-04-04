package com.project.cureconnect


import PaymentViewModel
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.project.cureconnect.DoctorPanel.MainDashBorad.DoctorAppointmentsScreen
import com.project.cureconnect.DoctorPanel.MainDashBorad.DoctorDashBoard
import com.project.cureconnect.pateints.navigationRoutes.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Locale

import com.project.cureconnect.ui.theme.CureConnectTheme
import com.razorpay.PaymentResultListener

import kotlin.math.sign


class MainActivity : ComponentActivity() , PaymentResultListener {
    private val paymentViewModel: PaymentViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CureConnectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        //LanguageSelector()  // Add language selector
                        Navigation()
                    }
                }
            }
        }
    }
    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentId", Toast.LENGTH_LONG).show()
        // TODO: Update appointment status to "Confirmed"
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
        // TODO: Handle payment failure logic
    }
}

@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    val languages = listOf("English", "हिन्दी")
    var selectedLanguage by remember { mutableStateOf(languages[0]) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Select Language", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(8.dp))

        languages.forEach { language ->
            Button(
                onClick = {
                    selectedLanguage = language
                    changeLanguage(context, if (language == "English") "en" else "hi")
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text = language)
            }
        }
    }
}

fun changeLanguage(context: Context, language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Restart activity to apply changes
    val activity = context as ComponentActivity
    activity.recreate() // This will restart the activity and apply the new language
}
fun getSavedLanguage(context: Context): String {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return prefs.getString("language", "English") ?: "English"
}