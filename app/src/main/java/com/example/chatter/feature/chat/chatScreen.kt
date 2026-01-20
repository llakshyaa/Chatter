package com.example.chatter.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chatter.ui.theme.DarkGray
import com.example.chatter.ui.theme.purple
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(
    navHostController: NavHostController,
    chatId: String,
    friendName: String
) {
    val viewModel: ChatViewModel = hiltViewModel()
    var inputText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(chatId) {
        viewModel.friendName = friendName
        viewModel.listenForMessages(chatId)
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkGray)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.friendName ?: "Friend",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }

            ChatInputSection(
                text = inputText,
                onTextChange = { inputText = it },
                onSendClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText.trim())
                        inputText = ""
                        keyboardController?.hide()
                    }
                }
            )
        }
    }
}

@Composable
fun ChatInputSection(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(DarkGray, RoundedCornerShape(24.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type your message...", color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkGray,
                unfocusedContainerColor = DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = {
                onSendClick()
                keyboardController?.hide()
            })
        )

        IconButton(onClick = {
            onSendClick()
            keyboardController?.hide()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isCurrentUser = message.senderId == FirebaseAuth.getInstance().currentUser?.uid
    val bubbleColor = if (isCurrentUser) purple else DarkGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bubbleColor)
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White
            )
        }
    }
}
