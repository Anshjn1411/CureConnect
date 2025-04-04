package com.project.cureconnect.pateints.mainScreens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun Shop() {
    val context = LocalContext.current
    val websiteUrl = "https://medi-store.vercel.app/"

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show()
    }
}