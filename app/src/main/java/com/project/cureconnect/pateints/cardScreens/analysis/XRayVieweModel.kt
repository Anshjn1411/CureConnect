package com.project.cureconnect.pateints.cardScreens.analysis

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.project.cureconnect.pateints.Api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream


class ImageUploadViewModel : ViewModel() {
    // Cloudinary Configuration
    private val cloudinary = Cloudinary(ObjectUtils.asMap(
        "cloud_name", "ds43vscit",
        "api_key", "852227254144386",
        "api_secret", "eUgehcz8shrlKevt6PP9SWdKh2k"
    ))

    private val _uploadResponse = MutableLiveData<String>()
    val uploadResponse: LiveData<String> get() = _uploadResponse

    private val _pdfFilePath = MutableLiveData<File>()
    val pdfFilePath: LiveData<File> get() = _pdfFilePath

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun uploadImageToCloudinary(context: Context, imageUri: Uri, user: String) {
        _isLoading.value = true
        val file = getFileFromUri(context, imageUri) ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Upload image to Cloudinary
                val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
                val imageUrl = result["secure_url"] as String

                Log.d("Cloudinary Upload", "Image URL: $imageUrl")
                sendImageUrlToBackend(context, imageUrl, user)

            } catch (e: Exception) {
                Log.e("Cloudinary Upload Error", e.message.toString())
                _isLoading.postValue(false)
            }
        }
    }


    private fun sendImageUrlToBackend(context: Context, imageUrl: String, user: String) {
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), """{"file_path":"$imageUrl"}""")

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitInstance.response2.uploadImageUrl(requestBody)
                if (response.isSuccessful) {
                    // Get the raw JSON response as a string
                    val responseBodyString = response.body()?.toString() ?: "{}"

                    Log.d("Upload", "Image URL sent successfully: $responseBodyString")

                    // Store the raw JSON response
                    _uploadResponse.value = responseBodyString

                    // Generate PDF from the response
                    generateECGReport(context, responseBodyString, user)

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

    private fun generateECGReport(context: Context, jsonResponse: String, user: String) {
        viewModelScope.launch {
            try {
                // Make sure we're working with a properly formatted JSON string
                val cleanedJson = jsonResponse.trim()

                Log.d("PDF Generation", "Using JSON: $cleanedJson")

                val reportGenerator = XRayReportGenerator()
                val pdfFile = reportGenerator.generateXRayReport(context, cleanedJson, user)

                _pdfFilePath.value = pdfFile
                _isLoading.value = false

                Toast.makeText(context, "ECG report generated successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("PDF Generation", "Error generating PDF: ${e.message}", e)
                Toast.makeText(context, "Failed to generate report: ${e.message}", Toast.LENGTH_SHORT).show()
                _isLoading.value = false
            }
        }
    }

    fun shareECGReport(context: Context, file: File) {
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

            context.startActivity(android.content.Intent.createChooser(intent, "Share ECG Report"))
        } catch (e: Exception) {
            Log.e("Share PDF", "Error: ${e.message}")
            Toast.makeText(context, "Failed to share report", Toast.LENGTH_SHORT).show()
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

    // For Android 10+ (API level 29+)
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun downloadPdfMediaStore(context: Context) {
        _pdfFilePath.value?.let { file ->
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "Xray_${System.currentTimeMillis()}.pdf")
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

                val destinationFile = File(downloadsDir, "ECG_Report_${System.currentTimeMillis()}.pdf")

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
