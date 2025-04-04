package com.project.cureconnect.pateints.cardScreens.consult

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.pateints.cardScreens.appoinmenet.Doctor
import com.project.cureconnect.pateints.cardScreens.appoinmenet.DoctorCard
import com.project.cureconnect.pateints.cardScreens.appoinmenet.fetchDoctorsFromFirestore

import com.project.cureconnect.pateints.navigationRoutes.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorList(navController: NavController) {
    var selectedItem by remember { mutableStateOf(2) }
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }

    LaunchedEffect(Unit) {
        fetchDoctorsFromFirestore { fetchedDoctors ->
            doctors = fetchedDoctors
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {  Text("Nearby Doctor",
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
                    onDoctorClick = { navController.navigate(Screen.chatScreen.routes) }
                )
                Spacer(Modifier.height(15.dp))
            }
        }
    }
}