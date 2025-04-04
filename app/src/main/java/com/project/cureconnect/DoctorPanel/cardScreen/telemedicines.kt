package com.project.cureconnect.DoctorPanel.cardScreen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

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

