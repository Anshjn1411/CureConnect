package com.project.cureconnect


import PaymentViewModel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.SplashScreenView

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController


import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager
import com.project.cureconnect.presentation.navigationRoutes.AppHost
import com.project.cureconnect.presentation.navigationRoutes.Screen
import java.util.Locale

import com.project.cureconnect.ui.theme.CureConnectTheme
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull

import kotlin.math.sign
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class MainActivity : ComponentActivity(), PaymentResultListener {

    private val paymentViewModel: PaymentViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {

        // ✅ Initialize JitsiMeet
        val serverURL = URL("https://meet.ffmuc.net")
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("pip.enabled", false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContent {
            CureConnectTheme {
                val context = LocalContext.current
                val sessionManager = remember { UserSessionManager(context) }
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<String?>(null) }

                var isLoading by remember { mutableStateOf(true) }
                splashScreen.setKeepOnScreenCondition { isLoading }

                LaunchedEffect(Unit) {
                    val startTime = System.currentTimeMillis()

                    val isLoggedIn = sessionManager.isLoggedInFlow.first()
                    val user = sessionManager.userData.first()
                    Log.d("MainActivity", "isLoggedIn = $isLoggedIn")
                    Log.d("MainActivity", "user = $user")
                    Log.d("MainActivity", "user?.role = ${user?.role}")


                    startDestination = when {
                        isLoggedIn && user?.role == "doctor" -> Screen.DoctorDashborad.routes
                        isLoggedIn && user?.role == "user" -> Screen.MainDashBoard.routes
                        else -> Screen.WelcomeScreen1.routes
                    }

                    isLoading = false
                    val endTime = System.currentTimeMillis()
                    Log.d("MainActivity", "Navigation decided in ${endTime - startTime}ms")
                }

                startDestination?.let {
                    AppHost(navController = navController, startDestination = it)
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentId", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }

    private fun onBroadcastReceived(intent: Intent) {
        val action = intent.action
        Log.d("Broadcast", "Received broadcast action: $action")

        when (action) {
            "YOUR_CUSTOM_ACTION" -> {

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