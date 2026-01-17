package com.example.chatter.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chatter.feature.navigation.BottomNavItem
import com.example.chatter.ui.theme.DarkGray
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {

    val viewModel = hiltViewModel<HomeViewModel>()
    val channels by viewModel.channels.collectAsState(initial = emptyList())
    var selectedIndex by remember { mutableIntStateOf(0) }

//    val navItems = listOf(
//        BottomNavItem.Home,
//        BottomNavItem.Search,
//        BottomNavItem.Notifications,
//        BottomNavItem.Profile
//    )

    LaunchedEffect(Unit) {
        viewModel.fetchUserChannels()
    }
    //
//    Scaffold(
//        containerColor = Color.Black,
//        bottomBar = {
//            NavigationBar(containerColor = DarkGray) {
//                navItems.forEachIndexed { index, item ->
//                    NavigationBarItem(
//                        selected = selectedIndex == index,
//                        onClick = { selectedIndex = index },
//                        icon = {
//                            Icon(
//                                imageVector = item.icon,
//                                contentDescription = item.label
//                            )
//                        },
//                        label = { Text(item.label) },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = Color.White,
//                            unselectedIconColor = Color.Gray,
//                            selectedTextColor = Color.White,
//                            unselectedTextColor = Color.Gray,
//                            indicatorColor = Color.Transparent
//                        )
//                    )
//                }
//            }
//        }
//    ) { paddingValues ->
//
//        Box(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//        ) {
//
//            when (navItems[selectedIndex]) {
//
//                BottomNavItem.Home -> {
//                    LazyColumn {
//                        item {
//                            Text(
//                                text = "Messages",
//                                color = Color.White,
//                                fontSize = 24.sp,
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }
//
//                        items(channels) { channel ->
//                            ChannelItem(
//                                channelName = channel.name,
//                                onClick = {
//                                    navHostController.navigate("chat/${channel.id}")
//                                }
//                            )
//                        }
//                    }
//                }
//
//                BottomNavItem.Search -> CenterScreen("Search Screen")
//                BottomNavItem.Notifications -> CenterScreen("Notifications Screen")
//                BottomNavItem.Profile -> CenterScreen("Profile Screen")
//            }
//        }
//    }
//}
    @Composable
    fun ChannelItem(channelName: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkGray)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Yellow.copy(alpha = 0.3f))
            ) {
                Text(
                    text = channelName.firstOrNull()?.uppercase(Locale.getDefault()) ?: "?",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = channelName,
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    @Composable
    fun CenterScreen(text: String) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontSize = 18.sp)
        }
    }
}