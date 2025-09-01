package com.project.cureconnect.presentation.screens.DoctorPanel.MainDashBorad


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.BottomNavItem
import com.project.cureconnect.presentation.screens.DoctorPanel.NavigationRoutesDoctor.Screen


@Composable
fun MainBottomBar(selectedItem: Int, navController: NavController, onItemSelected: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    NavigationBar(
        containerColor = Color.White,
        contentColor = primaryBlue,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem(Icons.Default.Home, "Home"),
            BottomNavItem(Icons.Default.Chat, "Health-Bot"),
            BottomNavItem(Icons.Default.DateRange, "Appointments"),
            BottomNavItem(Icons.Default.Person, "Profile")
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,

                        )
                },
                label = { Text(item.label, fontSize = 12.sp) },
                selected = selectedItem == index,
                onClick = {
                    onItemSelected(index)
                    when (index) {
                        0 -> navController.navigate(Screen.DoctorDashBoard.routes)
                        1 -> navController.navigate(Screen.search.routes)
                        2 -> showDialog = true
                        3 -> navController.navigate(Screen.profile.routes)
                    }
                }
            )
            UnderDevelopmentDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false }
            )
        }
    }
}
