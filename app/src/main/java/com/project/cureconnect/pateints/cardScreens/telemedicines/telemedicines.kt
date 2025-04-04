package com.project.cureconnect.pateints.cardScreens.telemedicines


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment

import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import kotlin.random.Random

@Composable
fun telemedicines(navController: NavController) {
    val context = LocalContext.current
    val websiteUrl = "https://video-call-final-git-main-orthodox-64s-projects.vercel.app/"

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show()
                }


}



