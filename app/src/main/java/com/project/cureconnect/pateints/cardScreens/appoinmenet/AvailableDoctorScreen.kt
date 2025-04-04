package com.project.cureconnect.pateints.cardScreens.appoinmenet

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.MainDashBoard
import com.project.cureconnect.pateints.mainScreens.MainBottomBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableDoctorsScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf(2) }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {

        fetchDoctorsFromFirestore { fetchedDoctors ->
            doctors = fetchedDoctors
            isLoading = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {  Text("Top Doctors",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold , color = Color.Black
                , modifier = Modifier.padding(start = 80.dp)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },


        containerColor = Color.White
    ) { paddingValues ->
        if (isLoading) {
            // Show loading spinner while fetching data
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Blue)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(doctors) { doctor ->

                    DoctorCard(
                        doctor = doctor,
                        onDoctorClick = { navController.navigate("doctor_details/${doctor.id}") }
                    )
                    Spacer(Modifier.height(15.dp))
                }
            }
        }
    }
}