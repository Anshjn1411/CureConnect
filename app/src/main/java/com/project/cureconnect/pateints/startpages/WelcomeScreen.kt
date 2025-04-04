package com.project.cureconnect.pateints.startpages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.R
import com.project.cureconnect.pateints.navigationRoutes.Screen
import com.project.cureconnect.ui.theme.comic

@Composable
fun Startpage(navController: NavController){
    var showMainScreen by remember { mutableStateOf(false) }

    if (showMainScreen) {


        Column(
            modifier = Modifier.fillMaxSize().padding(15.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(R.drawable.cureconnect_logo),
                modifier =
                Modifier.size(120.dp).padding(start = 15.dp, top=0.dp),
                contentDescription = "Civic Logo")
            Spacer(Modifier.height(20.dp))


            Text(
                text = "CureConnect",
                fontSize = 42.sp,
                color = colorResource(R.color.primary_blue_light),
                fontFamily = comic,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(15.dp))
            Text("Transform rural healthcare with seamless,\n" +
                    "technology-driven telemedicine services.\n" ,
                color = Color.Gray, fontSize = 20.sp , textAlign = TextAlign.Center )
            Spacer(Modifier.height(200.dp))
            buttonarea(navController)
            Spacer(Modifier.height(20.dp))

        }
    }
    else {
        SplashScreen ( navController, { showMainScreen = true })
    }

}

@Composable
fun buttonarea(navController: NavController){
    Column {
        Button(onClick = { navController.navigate(Screen.SignUp.routes) },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp)
                .fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(25),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_blue_light))

        ) {
            Text("GET STARTED" , fontWeight = FontWeight.Bold, color = Color.White,fontSize = 25.sp)

        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = { navController.navigate(Screen.SignIn.routes)  },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(10.dp)
                .fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(25),
            colors = ButtonDefaults.buttonColors(Color.Transparent)


        ) {
            Text("I ALREADY HAVE AN ACCOUNT" , fontWeight = FontWeight.Bold, color = colorResource(R.color.primary_blue_light),
                fontSize = 20.sp)

        } }

}

