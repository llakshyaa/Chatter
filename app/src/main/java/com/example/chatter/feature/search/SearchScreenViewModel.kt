package com.example.chatter.feature.search

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserSearchResult>>(emptyList())
    val users: StateFlow<List<UserSearchResult>> = _users.asStateFlow()

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _users.value = emptyList()
            return
        }

        val text = query.lowercase().trim()

        db.collection("users")
            .orderBy("username")
            .startAt(text)
            .endAt(text + "\uf8ff")
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val results = snapshot.documents.mapNotNull { doc ->
                    UserSearchResult(
                        uid = doc.getString("uid") ?: return@mapNotNull null,
                        name = doc.getString("name") ?: "",
                        username = doc.getString("username") ?: "",
                        email = doc.getString("email") ?: ""
                    )
                }
                _users.value = results
            }
    }
}
