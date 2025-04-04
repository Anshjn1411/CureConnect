package com.project.cureconnect.pateints.startpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.R
import com.project.cureconnect.ui.theme.comic

import kotlinx.coroutines.delay



@Composable
fun SplashScreen(navController: NavController , onTimeout: () -> Unit) {
    var showSplash by remember { mutableStateOf(true) }

    // Auto transition after delay
    LaunchedEffect(Unit) {
        delay(2000) // 2-second delay before auto-transition
        showSplash = false
        onTimeout()
    }
    if (showSplash) {
        Column(
            modifier = Modifier.fillMaxSize().background(colorResource(R.color.lightgreen))
                .padding(15.dp).clickable{

                    showSplash = false
                    onTimeout()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Image(
                painter = painterResource(R.drawable.cureconnect_logo),
                modifier =
                Modifier.size(200.dp).padding(start = 15.dp, top = 0.dp),
                contentDescription = "Eye Logo"
            )


            Text(
                text = "CureConnect",
                fontSize = 52.sp,
                color = Color.White,
                fontFamily = comic,
                fontWeight = FontWeight.Bold
            )

        }
    }
}


