package com.example.chatter.feature.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController


@Composable
fun NotificationScreen(
    navController: NavController,
) {
    val viewModel: NotificationViewModel = hiltViewModel()
    val requests by viewModel.requests.collectAsState() // List<FriendRequestUser>

    LaunchedEffect(Unit) {
        viewModel.observeRequests()
    }

    if (requests.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center,

        ) {
            Text(
                text = "No friend requests",
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color.Black)
        ) {
            items(requests) { user -> // 👈 now it's FriendRequestUser
                FriendRequestItem(
                    user = user, // 👈 pass the whole object
                    onAccept = { viewModel.accept(user.uid) },
                    onReject = { viewModel.reject(user.uid) }
                )
            }
        }
    }
}


@Composable
fun FriendRequestItem(
    user: FriendRequestUser,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth().background(Color.DarkGray)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // ---- Left: Avatar + Names ----
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Blue.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Name + UID column
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = user.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${user.username}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ---- Right: Accept / Reject buttons ----
            Row {
                IconButton(onClick = onAccept) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = Color.Green
                    )
                }
                IconButton(onClick = onReject) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Reject",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
