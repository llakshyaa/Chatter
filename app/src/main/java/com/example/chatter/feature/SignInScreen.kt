package com.example.chatter.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatter.R

@Composable
fun SignInScreen(navController: NavHostController) {

    val viewModel: SignInViewModel = hiltViewModel()

    // identifier can be email OR username
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.state.collectAsState()

    // ✅ Navigate on successful login
    LaunchedEffect(uiState) {
        if (uiState is SignInState.Success) {
            navController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(140.dp)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email OR Username field
            OutlinedTextField(
                value = identifier,
                onValueChange = { identifier = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email or Username") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (uiState) {
                is SignInState.Loading -> CircularProgressIndicator()
                else -> Button(
                    onClick = { viewModel.signIn(identifier.trim(), password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = identifier.isNotBlank() && password.isNotBlank()
                ) {
                    Text("Sign In")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { navController.navigate("signup") }
            ) {
                Text("Don't have an account? Sign Up")
            }

            // ✅ Error message
            if (uiState is SignInState.Error) {
                Text(
                    text = (uiState as SignInState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
