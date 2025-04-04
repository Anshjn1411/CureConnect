// Step 1: SpeechRecognitionHelper class
package com.project.cureconnect.pateints.mainScreens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.cureconnect.changeLanguage
import com.project.cureconnect.pateints.navigationRoutes.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SpeechRecognitionHelper(private val context: Context , navController: NavController ) {
    val navController = navController

    // Create state objects to track recognition status and results
    private val _isListening = mutableStateOf(false)
    val isListening = _isListening

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    private var speechRecognizer: SpeechRecognizer? = null

    // Initialize the speech recognizer
    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            createRecognizer()
        } else {
            Log.e("SpeechRecognition", "Speech recognition is not available on this device")
        }
    }

    private fun createRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            setupRecognitionListener()
        } catch (e: Exception) {
            Log.e("SpeechRecognition", "Error creating speech recognizer: ${e.message}")
        }
    }

    private fun setupRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognition", "Ready for speech")
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognition", "Speech started")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Can be used to show visual feedback about speech volume
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognition", "Speech ended")
                _isListening.value = false
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech input"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                    else -> "Unknown error"
                }
                Log.e("SpeechRecognition", "Error: $errorMessage ($error)")
                _isListening.value = false

                // If recognizer was busy, clean up and try again
                if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    destroy()
                    createRecognizer()
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    Log.d("SpeechRecognition", "Result: $recognizedText")
                    _recognizedText.value = recognizedText

                    // Here you can add logic to process the recognized text
                    processSpeechCommand(recognizedText , navController)
                }
                _isListening.value = false
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    Log.d("SpeechRecognition", "Partial: ${matches[0]}")
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        if (_isListening.value) {
            stopListening()
            return
        }

        if (speechRecognizer == null) {
            createRecognizer()
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }
            Log.d("SpeechRecognition", "Starting speech recognition")
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechRecognition", "Error starting speech recognition: ${e.message}")
            _isListening.value = false
        }
    }

    fun stopListening() {
        Log.d("SpeechRecognition", "Stopping speech recognition")
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun destroy() {
        Log.d("SpeechRecognition", "Destroying speech recognizer")
        speechRecognizer?.destroy()
        speechRecognizer = null
    }


    private fun processSpeechCommand(text: String, navController: NavController) {
        // Implement your command processing logic here
        // For example, navigate to different screens based on voice commands

        val command = text.lowercase(Locale.getDefault())
        Log.d("SpeechCommand", "Processing command: $command")

        // Example command processing:
        when {

            command.contains("telemedicine") || command.contains("telemedicines") -> {
                Log.d("SpeechCommand", "Appointment command detected")
                // Navigate to appointments
                navController.navigate(Screen.Telemedicine.routes)
            }
            command.contains("emergency") -> {
                Log.d("SpeechCommand", "Emergency command detected")
                navController.navigate(Screen.Emergency.routes)
                // Navigate to emergency
            }
            command.contains("profile") -> {
                Log.d("SpeechCommand", "Profile command detected")
                // Navigate to profile
                navController.navigate(Screen.profile.routes)
            }
            command.contains("appointment") -> {
                Log.d("SpeechCommand", "Profile command detected")
                // Navigate to profile
                navController.navigate(Screen.Appointment.routes)
            }
            command.contains("analysis") -> {
                Log.d("SpeechCommand", "Profile command detected")
                // Navigate to profile
                navController.navigate(Screen.Analysis.routes)
            }
            command.contains("health tips") -> {
                Log.d("SpeechCommand", "Profile command detected")
                // Navigate to profile
                navController.navigate(Screen.HealthTips.routes)
            }
            command.contains("consult") -> {
                Log.d("SpeechCommand", "Profile command detected")
                // Navigate to profile
                navController.navigate(Screen.Consult.routes)
            }
            command.contains("my appointment") || command.contains("my appointments") -> {
                Log.d("SpeechCommand", "Profile command detected")
                // Navigate to profile
                navController.navigate("my_appointments")
            }
            command.contains("ivr number") -> {
                Log.d("SpeechCommand", "IVR command detected")
                // Navigate to IVR
                navController.navigate("IVR")
            }
            command.contains("marathi", ignoreCase = true) -> {
                Log.d("SpeechCommand", "Marathi language command detected")
                changeLanguage(context, "mr") // Switch to Marathi
            }
            command.contains("hindi", ignoreCase = true) -> {
                Log.d("SpeechCommand", "Hindi language command detected")
                changeLanguage(context, "hi") // Switch to Hindi
            }
            command.contains("english", ignoreCase = true) || command.contains("English", ignoreCase = true) -> {
                Log.d("SpeechCommand", "English language command detected")

                changeLanguage(context, "en") // Switch to English

            }
            // Add more commands as needed
        }
    }
}
