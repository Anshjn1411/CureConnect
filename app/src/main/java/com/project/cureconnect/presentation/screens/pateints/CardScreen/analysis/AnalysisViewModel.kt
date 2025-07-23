package com.project.cureconnect.presentation.screens.pateints.CardScreen.analysis

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.sourceInformationMarkerEnd
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.cureconnect.data.api.Api.RetrofitInstance
import com.project.cureconnect.data.api.Api.RetrofitInstance.response3
import com.project.cureconnect.data.model.Constant.cloudinary.api_key
import com.project.cureconnect.data.model.Constant.cloudinary.api_secret
import com.project.cureconnect.data.model.Constant.cloudinary.cloud_name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AnalysisViewModel :ViewModel() {
    // Cloudinary Configuration
    private val cloudinary = Cloudinary(
        ObjectUtils.asMap(
        "cloud_name", cloud_name ,
        "api_key", api_key,
        "api_secret", api_secret
    ))
    val imageList = mapOf(
        0 to "X-ray",
        1 to "ECG",
        2 to "PET",
        3 to "MRI",
        4 to "Alzheimer's Detection",
        5 to "skin Dieases Analysis",
        6 to "Retinopathy Detection"
    )

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _uploadResponse = MutableLiveData<String>()
    val uploadResponse: LiveData<String> get() = _uploadResponse

    private val _pdfFilePath = MutableLiveData<File>()
    val pdfFilePath: LiveData<File> get() = _pdfFilePath

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun uploadImageToCloudinary(context: Context, imageUri: Uri, user: String , ID: Int ) {
        _isLoading.value = true
        val file = getFileFromUri(context, imageUri) ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
                val imageUrl = result["secure_url"] as String
                Log.d("UserName", "Image URL: $user")

                Log.d("Cloudinary Upload", "Image URL: $imageUrl")

                    sendImageToGemini(context, imageUrl, user , ID)


            } catch (e: Exception) {
                Log.e("Cloudinary Upload Error", e.message.toString())
                _isLoading.postValue(false)
            }
        }
    }
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        tempFile.createNewFile()

        inputStream.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    private fun sendImageUrlToBackend(context: Context, imageUrl: String, user: String , ID :Int) {
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), """{"file_path":"$imageUrl"}""")


        viewModelScope.launch(Dispatchers.Main) {
            if(ID==0 ){
                try {
                    val response = RetrofitInstance.response2.uploadImageUrl(requestBody)
                    if (response.isSuccessful) {
                        // Get the raw JSON response as a string
                        val responseBodyString = response.body()?.toString() ?: "{}"

                        Log.d("Upload", "Image URL sent successfully: $responseBodyString")

                        // Store the raw JSON response
                        _uploadResponse.value = responseBodyString

                        // Generate PDF from the response
                        generateReport(context, responseBodyString, user , ID)
                        saveDataToFirestore(user, imageUrl, responseBodyString)

                        Toast.makeText(context, "Analysis complete!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Upload", "Failed to send URL: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Failed to analyze ECG", Toast.LENGTH_SHORT).show()
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    Log.e("Upload Error", e.message.toString())
                    _isLoading.value = false
                }

            }else if(ID == 1){
                try {
                    val response = RetrofitInstance.response.uploadImageUrl(requestBody)
                    if (response.isSuccessful) {
                        // Get the raw JSON response as a string
                        val responseBodyString = response.body()?.toString() ?: "{}"

                        Log.d("Upload", "Image URL sent successfully: $responseBodyString")

                        // Store the raw JSON response
                        _uploadResponse.value = responseBodyString


                        // Generate PDF from the response
                        generateReport(context, responseBodyString, user,ID)
                        saveDataToFirestore(user, imageUrl, responseBodyString)

                        Toast.makeText(context, "Analysis complete!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("Upload", "Failed to send URL: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Failed to analyze ECG", Toast.LENGTH_SHORT).show()
                        _isLoading.value = false
                    }
                } catch (e: Exception) {
                    Log.e("Upload Error", e.message.toString())
                    _isLoading.value = false
                }
            }

        }

    }
    fun sendImageToGemini(context: Context, imageUrl: String, user: String, ID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Download the image
                val connection = URL(imageUrl).openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap == null) {
                    Log.e("Image Error", "Failed to decode image.")
                    return@launch
                }

                // 2. Convert to base64
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

                // 3. Prepare JSON using org.json
                val inlineData = JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", base64Image)
                }

                val part1 = JSONObject().apply { put("text", "Describe the image in detail.") }
                val part2 = JSONObject().apply { put("inlineData", inlineData) }

                val partsArray = JSONArray().apply {
                    put(part1)
                    put(part2)
                }

                val contentItem = JSONObject().apply { put("parts", partsArray) }
                val contentsArray = JSONArray().apply { put(contentItem) }

                val requestBodyJson = JSONObject().apply {
                    put("contents", contentsArray)
                }

                val requestBody = requestBodyJson.toString()
                    .toRequestBody("application/json".toMediaType())

                viewModelScope.launch {
                    try {
                        val apikey = "AIzaSyASSY9fkUZY2Q9cYsCd-mTMK0sr98lPh30"
                        val result = response3.uploadImage(apikey , requestBody)
                        Log.d("Retrofit", "Success: $result")

                        if (result.isSuccessful) {
                            val data = result.body()
                            val onlyText = result.body()?.candidates?.get(0)?.content?.parts?.get(0)?.text
                            _uploadResponse.value = onlyText.toString()
                            generateReport(context, onlyText.toString(), user, ID)
                            saveDataToFirestore(user, imageUrl, onlyText.toString())

                            Log.d("Retrofit", "Success: $data")
                        } else {
                            Log.e("Retrofit", "Error: ${result.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        Log.e("Retrofit", "Exception: ${e.message}")
                    }
                }

            }
             catch (e: Exception) {
                Log.e("LLM API Error", "Exception occurred: ${e.message}", e)
            }
        }
    }

    private fun saveDataToFirestore(userId: String, imageUrl: String, response: String) {
        val recordId = firestore.collection("patients").document(userId)
            .collection("history").document().id

        val data = mapOf(
            "imageUrl" to imageUrl,
            "response" to response,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("patients")
            .document(userId)
            .collection("history")
            .document(recordId)
            .set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Patient history saved successfully")
                _isLoading.postValue(false)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore Error", "Failed to save patient history: ${e.message}")
                _isLoading.postValue(false)
            }
    }

    private fun generateReport(context: Context, jsonResponse: String, user: String , ID: Int) {
        viewModelScope.launch {
            try {
                // Make sure we're working with a properly formatted JSON string
                val cleanedJson = jsonResponse.trim()

                Log.d("PDF Generation", "Using JSON: $cleanedJson")

                val reportGenerator = ReportGenerator()
                val pdfFile = reportGenerator.generateReport(context, cleanedJson, user, ID)

                _pdfFilePath.value = pdfFile
                _isLoading.value = false
                Log.d("PDF Generation", "PDF generated successfully: ${pdfFile.absolutePath}")

                Toast.makeText(context, "${imageList.get(ID)} report generated successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("PDF Generation", "Error generating PDF: ${e.message}", e)
                Toast.makeText(context, "Failed to generate report: ${e.message}", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
            }
        }
    }

    fun shareReport(context: Context, file: File ) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(android.content.Intent.createChooser(intent, "Share Report"))
        } catch (e: Exception) {
            Log.e("Share PDF", "Error: ${e.message}")
            Toast.makeText(context, "Failed to share report", Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadPdf(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - use MediaStore
            downloadPdfMediaStore(context)
        } else {
            // Android 9 and below - need runtime permission
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                downloadPdfLegacy(context)
            } else {
                // Permission not granted, notify the UI to request it
                Toast.makeText(
                    context,
                    "Storage permission required to download PDF",
                    Toast.LENGTH_LONG
                ).show()
                _requestPermission.postValue(true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadPdfMediaStore(context: Context) {
        _pdfFilePath.value?.let { file ->
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "Report_${System.currentTimeMillis()}.pdf")
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val contentResolver = context.contentResolver
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)

                    Toast.makeText(context, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("PDF Download", "Error: ${e.message}")
                Toast.makeText(context, "Failed to download PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "No PDF file available to download", Toast.LENGTH_SHORT).show()
        }
    }

    // For Android 9 and below
    private fun downloadPdfLegacy(context: Context) {
        _pdfFilePath.value?.let { file ->
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }

                val destinationFile = File(downloadsDir, "Report_${System.currentTimeMillis()}.pdf")

                file.inputStream().use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }

                // Make the file visible in the file system
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(destinationFile.absolutePath),
                    arrayOf("application/pdf"),
                    null
                )

                Toast.makeText(context, "PDF saved to Downloads folder", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("PDF Download", "Error: ${e.message}")
                Toast.makeText(context, "Failed to download PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "No PDF file available to download", Toast.LENGTH_SHORT).show()
        }
    }

    // Add this LiveData to request permission from the UI layer
    private val _requestPermission = MutableLiveData<Boolean>()
    val requestPermission: LiveData<Boolean> get() = _requestPermission


}