package com.project.cureconnect.presentation.screens.pateints.CardScreen.consult

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet.AppoinmenetViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun consult (navController: NavController , appoinmenetViewModel: AppoinmenetViewModel){
    DoctorList(navController , appoinmenetViewModel)
}