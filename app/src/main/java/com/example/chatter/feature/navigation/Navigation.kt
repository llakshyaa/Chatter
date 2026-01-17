package com.example.chatter.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chatter.feature.navigation.BottomNavItem
import com.example.chatter.ui.theme.DarkGray

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Notifications,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = DarkGray,   // Dark background
        tonalElevation = 4.dp         // Slight elevation for shadow
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(BottomNavItem.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color.White else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (currentRoute == item.route) Color.White else Color.Gray
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
