package com.project.cureconnect.presentation.screens.pateints.CardScreen.analysis

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.cureconnect.data.datastore.UserSessionLayer.CachedUser
import com.project.cureconnect.data.datastore.UserSessionLayer.UserSessionManager


@Composable
fun AnalysisScreen(navController: NavController, analysisItem: AnalysisItem) {
    val context : Context = LocalContext.current
    var name by remember { mutableStateOf("") }
    val sessionManager = remember { UserSessionManager(context) }
    var id by remember { mutableStateOf("") }


    // Observe cached user from DataStore
    LaunchedEffect(Unit) {
        sessionManager.userData.collect { user ->
            user?.let {
                name = it.name
                id = it.uid
                Log.d("UserSession", "Name: $name, ID: $id")
            }
        }
    }



    val scrollState = rememberScrollState()
    var isDarkMode by remember { mutableStateOf(false) }

    var idImage by remember { mutableStateOf(0) }
    idImage = analysisItem.id.toInt()

    val backgroundColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val cardColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White

    val viewModel: AnalysisViewModel = viewModel()
    val pdfFile by viewModel.pdfFilePath.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            viewModel.uploadImageToCloudinary(context, it, name, analysisItem.id.toInt())
        }
    }

    val imageList = mapOf(
        0 to "X-ray",
        1 to "ECG",
        2 to "PET",
        3 to "MRI",
        4 to "Alzheimer's Detection",
        5 to "skin Dieases Analysis",
        6 to "Retinopathy Detection"
    )



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            ExtraArea(navController)
            ExtraArea2(navController)

            Spacer(modifier = Modifier.height(24.dp))

                // Image Upload Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Upload ${imageList.get(analysisItem.id.toInt())} Image for Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Image preview or upload prompt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 2.dp,
                                    color = Color.Blue.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(if (isDarkMode) Color(0xFF2A2A2A) else Color(0xFFF0F0F0))
                                .clickable { galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Selected image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Upload image",
                                        tint = Color.Blue,
                                        modifier = Modifier.size(48.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Tap to upload an image",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { selectedImageUri?.let { viewModel.uploadImageToCloudinary(context, it, name, analysisItem.id.toInt())
                                 } },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedImageUri != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Analyze Image",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        if (viewModel.uploadResponse.value != null && pdfFile != null ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    pdfFile?.let { file ->
                                        viewModel.shareReport(context, file)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Share PDF Report",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.downloadPdf(context)
                                     },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Download PDF Report",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }

                        // Add a loading indicator when analysis is in progress
                        if (isLoading) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Analyzing ${imageList.get(analysisItem.id.toInt())}...",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = textColor
                            )
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
