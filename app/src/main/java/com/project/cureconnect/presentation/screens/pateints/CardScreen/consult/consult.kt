package com.project.cureconnect.presentation.screens.pateints.CardScreen.consult

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun consult (navController: NavController){
    DoctorList(navController)
}