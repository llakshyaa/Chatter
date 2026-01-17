package com.example.chatter.feature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        "home",
        "Home",
        Icons.Default.Home
    )

    object Search : BottomNavItem(
        "search",
        "Search",
        Icons.Default.Search
    )

    object Notifications : BottomNavItem(
        "notifications",
        "Notifications",
        Icons.Default.Notifications
    )

    object Profile : BottomNavItem(
        "profile",
        "Profile",
        Icons.Default.Person
    )
}
