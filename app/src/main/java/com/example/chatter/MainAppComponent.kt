package com.example.chatter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatter.feature.SignInScreen
import com.example.chatter.feature.SignUpScreen
import com.example.chatter.feature.chat.ChatScreen
import com.example.chatter.feature.navigation.AppNavGraph
import com.example.chatter.navigation.BottomNavBar

@Composable
fun MainApp() {
    val rootNavController = rememberNavController()

    Surface(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = rootNavController,
            startDestination = "login"
        ) {

            // LOGIN FLOW
            composable("login") {
                SignInScreen(navController = rootNavController)
            }

            composable("signup") {
                SignUpScreen(navController = rootNavController)
            }

            // MAIN APP AFTER LOGIN → contains bottom nav
            composable("main") {
                MainScreen(rootNavController)
            }

            // CHAT SCREEN (full screen, no bottom nav)
            composable(
                route = "chat/{channelId}",
                arguments = listOf(
                    navArgument("channelId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
                ChatScreen(rootNavController, channelId)
            }
        }
    }
}

@Composable
fun MainScreen(rootNavController: NavController) {
    val navController = rememberNavController() // For bottom nav

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            paddingValues = paddingValues
        )
    }
}