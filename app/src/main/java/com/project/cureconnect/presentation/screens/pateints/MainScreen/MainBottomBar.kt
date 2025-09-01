package com.project.cureconnect.presentation.screens.pateints.MainScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.cureconnect.BottomNavItem
import com.project.cureconnect.presentation.navigationRoutes.Screen
import com.project.cureconnect.primaryBlue
import com.project.cureconnect.ui.theme.Elevations

@Composable
fun MainBottomBar(
    selectedItem: Int,
    navController: NavController,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp // Using standard Material 3 elevation
    ) {
        val items = listOf(
            BottomNavItem(Icons.Default.Home, "Home"),
            BottomNavItem(Icons.Default.Chat, "Health-Bot"),
            BottomNavItem(Icons.Default.ShoppingCart, "Shop"),
            BottomNavItem(Icons.Default.Person, "Profile")
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedItem == index,
                onClick = {
                    onItemSelected(index)
                    when (index) {
                        0 -> navController.navigate(Screen.MainDashBoard.routes)
                        1 -> navController.navigate(Screen.search.routes)
                        2 -> navController.navigate("shop")
                        3 -> navController.navigate(Screen.profile.routes)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}