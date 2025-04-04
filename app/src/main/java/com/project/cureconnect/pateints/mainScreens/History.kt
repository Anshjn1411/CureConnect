package com.project.cureconnect.pateints.mainScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.FirebaseFirestore
import login.AuthViewModel

// Data class matching Firebase document structure
data class PatientHistoryRecord(
    val recordId: String,
    val imageUrl: String,
    val response: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHistoryPage(
    navController: NavController,
userId : String
) {
    Log.d("userdataata", userId)
    var historyRecords by remember { mutableStateOf(listOf<PatientHistoryRecord>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch patient history from Firestore
    LaunchedEffect(userId) {
        val db = FirebaseFirestore.getInstance()

        // Get the "history" collection directly
        db.collection("patients")
            .document(userId)
            .collection("history")
            .get()
            .addOnSuccessListener { documents ->
                historyRecords = documents.map { document ->
                    PatientHistoryRecord(
                        recordId = document.id,
                        imageUrl = document.getString("imageUrl") ?: "",
                        response = document.getString("response") ?: "No response available"
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                println("Error fetching history: ${exception.message}")
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient History" , color = Color.Black , fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = {navController.navigateUp()}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (historyRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No history records found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(historyRecords) { record ->
                    ImprovedHistoryRecordItem(record)
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ImprovedHistoryRecordItem(record: PatientHistoryRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Medical Image - Full Width
            if (record.imageUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(record.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Medical Image",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // Placeholder for missing image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No Image Available",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Response Text - Below Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                Text(
                    text = record.response,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}