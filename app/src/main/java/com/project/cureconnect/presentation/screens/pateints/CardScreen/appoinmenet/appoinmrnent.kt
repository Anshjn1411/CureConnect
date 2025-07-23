package com.project.cureconnect.presentation.screens.pateints.CardScreen.appoinmenet

import android.util.Log
import androidx.compose.runtime.*

import androidx.compose.runtime.Composable

import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.cureconnect.R




@Composable
fun appoinmrnent (navController: NavController){

    AvailableDoctorsScreen(navController)
}



