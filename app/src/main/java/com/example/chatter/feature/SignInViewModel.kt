package com.example.chatter.feature

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _state =
        MutableStateFlow<SignInState>(SignInState.Nothing)

    val state: StateFlow<SignInState> = _state.asStateFlow()

    fun signIn(identifier: String, password: String) {
        _state.value = SignInState.Loading

        if (identifier.contains("@")) {
            signInWithEmail(identifier, password)
        } else {
            signInWithUsername(identifier.lowercase(), password)
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = SignInState.Success
                } else {
                    _state.value =
                        SignInState.Error(
                            task.exception?.localizedMessage
                                ?: "Login failed"
                        )
                }
            }
    }

    private fun signInWithUsername(username: String, password: String) {
        db.collection("usernames")
            .document(username)
            .get()
            .addOnSuccessListener { doc ->

                if (!doc.exists()) {
                    _state.value =
                        SignInState.Error("Username not found")
                    return@addOnSuccessListener
                }

                val uid = doc.getString("uid") ?: return@addOnSuccessListener

                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { userDoc ->
                        val email = userDoc.getString("email")
                        if (email.isNullOrEmpty()) {
                            _state.value =
                                SignInState.Error("Email not found")
                            return@addOnSuccessListener
                        }

                        signInWithEmail(email, password)
                    }
                    .addOnFailureListener {
                        _state.value =
                            SignInState.Error("Failed to fetch user")
                    }
            }
            .addOnFailureListener {
                _state.value =
                    SignInState.Error("Username lookup failed")
            }
    }
}

sealed class SignInState {
    object Nothing : SignInState()
    object Loading : SignInState()
    object Success : SignInState()
    data class Error(val message: String) : SignInState()
}
