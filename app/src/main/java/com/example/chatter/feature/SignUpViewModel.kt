package com.example.chatter.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 🔹 STATE
sealed class SignUpState {
    object Nothing : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth,              // ✅ injected
    private val db: FirebaseFirestore             // ✅ injected
) : ViewModel() {

    private val _state =
        MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun signUp(
        name: String,
        email: String,
        password: String,
        username: String
    ) {
        _state.value = SignUpState.Loading

        val cleanUsername = username.lowercase().trim()

        // 1️⃣ Check username uniqueness
        db.collection("usernames")
            .document(cleanUsername)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {
                    _state.value = SignUpState.Error("Username already taken")
                    return@addOnSuccessListener
                }

                // 2️⃣ Create auth user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        viewModelScope.launch {

                            if (task.isSuccessful) {
                                val uid = task.result?.user?.uid
                                if (uid == null) {
                                    _state.value =
                                        SignUpState.Error("User ID is null")
                                    return@launch
                                }

                                val user = hashMapOf(
                                    "uid" to uid,
                                    "name" to name,
                                    "username" to cleanUsername,
                                    "email" to email
                                )

                                // 3️⃣ Save user profile
                                db.collection("users")
                                    .document(uid)
                                    .set(user)
                                    .addOnSuccessListener {

                                        // 4️⃣ Reserve username
                                        db.collection("usernames")
                                            .document(cleanUsername)
                                            .set(mapOf("uid" to uid))
                                            .addOnSuccessListener {
                                                _state.value = SignUpState.Success
                                            }
                                            .addOnFailureListener { e ->
                                                _state.value =
                                                    SignUpState.Error(
                                                        e.localizedMessage
                                                            ?: "Username reservation failed"
                                                    )
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        _state.value =
                                            SignUpState.Error(
                                                e.localizedMessage
                                                    ?: "Failed to save user"
                                            )
                                    }
                            } else {
                                val exception = task.exception
                                val message = when (exception) {
                                    is FirebaseAuthUserCollisionException ->
                                        "This email is already registered"
                                    else ->
                                        exception?.localizedMessage
                                            ?: "Sign-up failed"
                                }
                                _state.value = SignUpState.Error(message)
                            }
                        }
                    }
            }
            .addOnFailureListener { e ->
                _state.value =
                    SignUpState.Error(
                        e.localizedMessage ?: "Username check failed"
                    )
            }
    }
}
