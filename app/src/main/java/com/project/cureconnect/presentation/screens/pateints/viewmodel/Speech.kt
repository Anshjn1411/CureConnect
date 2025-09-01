package com.project.cureconnect.presentation.screens.pateints.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.project.cureconnect.changeLanguage
import com.project.cureconnect.presentation.navigationRoutes.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class SpeechRecognitionHelper(private val context: Context, navController: NavController) {
    private val navController = navController

    private val _isListening = mutableStateOf(false)
    val isListening = _isListening

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    private var speechRecognizer: SpeechRecognizer? = null

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

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                Log.d("SpeechRecognition", "Speech ended")
                _isListening.value = false
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    else -> "Unknown error"
                }
                Log.e("SpeechRecognition", "Error: $message ($error)")
                _isListening.value = false
                if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    destroy()
                    createRecognizer()
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    _recognizedText.value = recognizedText
                    processSpeechCommand(recognizedText, navController)
                }
                _isListening.value = false
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        if (_isListening.value) {
            stopListening()
            return
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("SpeechRecognition", "Microphone permission not granted")
            requestAudioPermission()
            return
        }

        if (speechRecognizer == null) createRecognizer()

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }
            Log.d("SpeechRecognition", "Starting speech recognition")
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechRecognition", "Error: ${e.message}")
            _isListening.value = false
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun requestAudioPermission() {
        if (context is Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1001
            )
        } else {
            Log.e("SpeechRecognition", "Context is not an Activity. Cannot request permissions.")
        }
    }

    private fun processSpeechCommand(text: String, navController: NavController) {
        val command = text.lowercase(Locale.getDefault())
        when {
            command.contains("telemedicine") -> navController.navigate(Screen.Telemedicine.routes)
            command.contains("emergency") -> navController.navigate(Screen.Emergency.routes)
            command.contains("profile") -> navController.navigate(Screen.profile.routes)
            command.contains("appointment") -> navController.navigate(Screen.Appointment.routes)
            command.contains("analysis") -> navController.navigate(Screen.Analysis.routes)
            command.contains("health tips") -> navController.navigate(Screen.HealthTips.routes)
            command.contains("consult") -> navController.navigate(Screen.Consult.routes)
            command.contains("my appointment") -> navController.navigate("my_appointments")
            command.contains("ivr number") -> navController.navigate("IVR")
            command.contains("marathi") -> changeLanguage(context, "mr")
            command.contains("hindi") -> changeLanguage(context, "hi")
            command.contains("english") -> changeLanguage(context, "en")
        }
    }
}
