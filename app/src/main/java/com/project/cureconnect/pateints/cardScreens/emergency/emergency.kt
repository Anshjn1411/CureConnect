package com.project.cureconnect.pateints.cardScreens.emergency

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun emergency (navController: NavController){
    val context = LocalContext.current
    val websiteUrl = "https://sachinpro.onrender.com/"

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show()
                }
            }