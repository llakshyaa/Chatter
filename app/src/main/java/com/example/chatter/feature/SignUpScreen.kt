package com.example.chatter.feature

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chatter.R

@Composable
fun SignUpScreen(navController: NavHostController) {

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }   // ✅ ADDED
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val viewModel: SignUpViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SignUpState.Success) {
            navController.navigate("homescreen") {
                popUpTo("signup") { inclusive = true }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Name") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ USERNAME FIELD
            OutlinedTextField(
                value = username,
                onValueChange = { username = it.lowercase() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Username") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = password != confirmPassword &&
                        password.isNotEmpty() &&
                        confirmPassword.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.signUp(
                        name = name,
                        email = email,
                        password = password,
                        username = username
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled =
                    name.isNotBlank() &&
                            username.length >= 3 &&
                            email.isNotBlank() &&
                            password == confirmPassword &&
                            password.length >= 6
            ) {
                Text("Sign Up")
            }

            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text("Already have an account? Sign In")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is SignUpState.Loading -> {
                    Text("Signing up...", color = Color.Gray)
                }
                is SignUpState.Error -> {
                    Text(
                        (uiState as SignUpState.Error).message,
                        color = Color.Red
                    )
                }
                else -> Unit
            }
        }
    }
}
