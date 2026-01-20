package com.example.chatter.feature.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chatter.feature.chat.ChatScreen
import com.example.chatter.feature.home.HomeScreen
import com.example.chatter.feature.navigation.BottomNavItem
import com.example.chatter.feature.notification.NotificationScreen
import com.example.chatter.feature.profile.ProfileScreen
import com.example.chatter.feature.search.SearchScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {

        composable(BottomNavItem.Home.route) {
            HomeScreen(navController)
        }

        composable(BottomNavItem.Search.route) {
            SearchScreen(navController)   // ✅ FIX
        }

        composable(BottomNavItem.Notifications.route) {
            NotificationScreen(navController)          // OK (no navigation inside)
        }

        composable(BottomNavItem.Profile.route) {
            ProfileScreen(navController)  // ✅ FIX
        }
        composable(
            route = "chat/{chatId}/{friendName}"
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val friendName = backStackEntry.arguments?.getString("friendName") ?: ""

            ChatScreen(
                navHostController = navController,
                chatId = chatId,
                friendName = friendName
            )
        }
    }
}
