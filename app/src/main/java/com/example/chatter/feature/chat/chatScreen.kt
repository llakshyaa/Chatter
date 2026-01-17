package com.example.chatter.feature.chat

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
//import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.chatter.R
import com.example.chatter.ui.theme.DarkGray
import com.example.chatter.ui.theme.purple
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.core.content.ContextCompat
//import androidx.compose.material.icons.filled.Image

@Composable
fun ChatScreen(navHostController: NavHostController, channelId: String) {

    val context = LocalContext.current

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    val cameraImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cameraImageUri.value?.let { uri ->
                    Log.d("ChatScreen", "Image captured: $uri")
                }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                Log.d("ChatScreen", "Gallery image selected: $it")
            }
        }
    )

    Scaffold(
        containerColor = Color.Black
    ) { paddingValues ->

        val viewModel: ChatViewModel = hiltViewModel()

        LaunchedEffect(key1 = true) {
            viewModel.listenForMessages(channelId)
        }

        val messages = viewModel.messages.collectAsState()

        val chooserDialog = remember { mutableStateOf(false) }

        fun createImageUri(): Uri? {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = ContextCompat.getExternalFilesDirs(
                context, // Use LocalContext.current or pass context
                Environment.DIRECTORY_PICTURES
            ).first()
            val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            cameraImageUri.value = uri // Store the Uri in the state for later use
            return uri
        }

        val permissionLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    val uri = createImageUri()
                    if (uri != null) {
                        cameraImageLauncher.launch(uri)
                    }
                }
            }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f) // Makes the LazyColumn take up all available space
            ) {
                items(messages.value) { msgObj ->
                    ChatBubble(message = msgObj)
                }
            }
            // The ChatInputSection is separated to its own composable for better readability
            ChatInputSection(
                onSendMessage = { message ->
                    viewModel.sendMessage(channelId, message)
                },
                onImageClicked = {
                    chooserDialog.value = true
                }
            )
        }

        if (chooserDialog.value) {
            ContentSelectionDialog(
                onCameraSelected = {
                    chooserDialog.value = false
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        val uri = createImageUri()
                        if (uri != null) {
                            cameraImageLauncher.launch(uri)
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onGallerySelected = {
                    galleryLauncher.launch("image/*")
                    chooserDialog.value = false
                },
                onDismiss = { chooserDialog.value = false }
            )
        }
    }
}

@Composable
fun ChatInputSection(
    onSendMessage: (String) -> Unit,
    onImageClicked: () -> Unit
) {
    val msg = remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray, RoundedCornerShape(24.dp)), // Adjusted background for a better UI
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onImageClicked) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Attach image",
                tint = Color.Black
            )
        }

        TextField(
            value = msg.value,
            onValueChange = { msg.value = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = "Type your message...") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    hideKeyboardController?.hide()
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkGray,
                unfocusedContainerColor = DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                cursorColor = Color.White,
            )
        )
        IconButton(onClick = {
            if (msg.value.isNotBlank()) {
                onSendMessage(msg.value.trim())
                msg.value = ""
                hideKeyboardController?.hide()
            }
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun ContentSelectionDialog(
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Select Your Source?", color = Color.White) },
        text = {
            Text(
                text = "Would you like to pick an image from the gallery or take a photo?",
                color = Color.White
            )
        },
        confirmButton = {
            TextButton(onClick = onCameraSelected) {
                Text("Camera", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onGallerySelected) {
                Text("Gallery", color = Color.White)
            }
        },
        containerColor = DarkGray // Added a color for the AlertDialog container for better visibility
    )
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) {
        purple
    } else {
        DarkGray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Row(
            modifier = Modifier
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isCurrentUser) {
                Image(
                    painter = painterResource(
                        id = R.drawable.person
                    ),
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(43.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp)) // Adds space between the image and the bubble
            }

            Text(
                text = message.text,
                color = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .background(bubbleColor, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
        }
    }
}