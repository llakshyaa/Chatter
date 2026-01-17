package com.example.chatter.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavController
import com.example.chatter.ui.theme.DarkGray
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val users by viewModel.users.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // 🔍 SEARCH BAR
        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.searchUsers(it)
            },
            placeholder = { Text("Search by username") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(30.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkGray,
                unfocusedContainerColor = DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedPlaceholderColor = Color.Gray,
                unfocusedPlaceholderColor = Color.Gray,
            )
        )

        // 🔽 SEARCH RESULTS
        LazyColumn {
            items(users) { user ->
                UserSearchItem(
                    user = user,
                    onClick = {
                        // 👉 Navigate to profile screen
                        navController.navigate("profile/${user.uid}")
                    }
                )
            }
        }
    }
}

@Composable
fun UserSearchItem(
    user: UserSearchResult,
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

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Blue.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.firstOrNull()?.uppercase(Locale.getDefault()) ?: "?",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = user.name,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "@${user.username}",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }
}