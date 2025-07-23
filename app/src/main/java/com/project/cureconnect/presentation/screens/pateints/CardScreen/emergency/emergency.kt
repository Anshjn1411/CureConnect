package com.project.cureconnect.presentation.screens.pateints.CardScreen.emergency

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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