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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chatter.ui.theme.DarkGray
import java.util.*

@Composable
fun HomeScreen(navHostController: NavHostController) {

    val viewModel: HomeViewModel = hiltViewModel()
    val friends by viewModel.friends.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.observeFriends()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // 🔹 Header
        Text(
            text = "Messages",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // 🔹 Friends / Chats list
        if (friends.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No chats yet",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn {
                items(friends) { friend ->
                    ChatItem(
                        friend = friend,
                        onClick = {
                            val encodedName = java.net.URLEncoder.encode(friend.name, "UTF-8")
                            navHostController.navigate("chat/${friend.chatId}/$encodedName")
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun ChatItem(
    friend: FriendUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGray)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🟡 Avatar
        Box(
            modifier = Modifier
                .padding(12.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.Yellow.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = friend.name.firstOrNull()?.uppercase(Locale.getDefault()) ?: "?",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 🟢 Name + last message
        Column(
            modifier = Modifier
                .padding(end = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = friend.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = friend.lastMessage.ifBlank { "Say hi 👋" },
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}
