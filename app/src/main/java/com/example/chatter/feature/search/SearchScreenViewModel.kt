package com.example.chatter.feature.search

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    private val auth = FirebaseAuth.getInstance()
    private val myId: String
        get() = auth.currentUser?.uid ?: ""

    /** SEARCH USERS */
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
                    val uid = doc.getString("uid") ?: return@mapNotNull null
                    val requestsSent = doc.get("requestsSent") as? List<String> ?: emptyList()
                    val requestsReceived = doc.get("requestsReceived") as? List<String> ?: emptyList()
                    val friends = doc.get("friends") as? List<String> ?: emptyList()

                    // Determine friend state relative to current user
                    val state = when {
                        friends.contains(myId) -> FriendState.FRIENDS
                        requestsReceived.contains(myId) -> FriendState.REQUEST_RECEIVED
                        requestsSent.contains(myId) -> FriendState.REQUEST_SENT
                        else -> FriendState.NONE
                    }

                    UserSearchResult(
                        uid = uid,
                        name = doc.getString("name") ?: "",
                        username = doc.getString("username") ?: "",
                        email = doc.getString("email") ?: "",
                        friendState = state
                    )
                }
                _users.value = results
            }
    }

    /** SEND FRIEND REQUEST */
    fun sendFriendRequest(targetUid: String) {
        // Add to my requestsSent
        db.collection("users").document(myId)
            .update("requestsSent", FieldValue.arrayUnion(targetUid))

        // Add to target's requestsReceived
        db.collection("users").document(targetUid)
            .update("requestsReceived", FieldValue.arrayUnion(myId))

        updateUserState(targetUid, FriendState.REQUEST_SENT)
    }

    /** CANCEL FRIEND REQUEST */
    fun cancelFriendRequest(targetUid: String) {
        db.collection("users").document(myId)
            .update("requestsSent", FieldValue.arrayRemove(targetUid))

        db.collection("users").document(targetUid)
            .update("requestsReceived", FieldValue.arrayRemove(myId))

        updateUserState(targetUid, FriendState.NONE)
    }

    /** ACCEPT FRIEND REQUEST */
    fun acceptFriendRequest(targetUid: String) {
        // Remove from requests arrays
        db.collection("users").document(myId)
            .update("requestsReceived", FieldValue.arrayRemove(targetUid))
        db.collection("users").document(targetUid)
            .update("requestsSent", FieldValue.arrayRemove(myId))

        // Add to friends array
        db.collection("users").document(myId)
            .update("friends", FieldValue.arrayUnion(targetUid))
        db.collection("users").document(targetUid)
            .update("friends", FieldValue.arrayUnion(myId))

        updateUserState(targetUid, FriendState.FRIENDS)
    }

    /** OPEN CHAT WITH FRIEND */
    fun openChat(targetUid: String, onNavigate: (String) -> Unit) {
        val chatId = listOf(myId, targetUid).sorted().joinToString("_")

        val chatRef = db.collection("chats").document(chatId)
        chatRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                chatRef.set(
                    mapOf(
                        "members" to listOf(myId, targetUid),
                        "createdAt" to Timestamp.now()
                    )
                )
            }
            onNavigate(chatId)
        }
    }

    /** INTERNAL: Update friendState locally in UI */
    private fun updateUserState(targetUid: String, newState: FriendState) {
        _users.value = _users.value.map { user ->
            if (user.uid == targetUid) user.copy(friendState = newState)
            else user
        }
    }
}

/** FRIEND STATE ENUM */
enum class FriendState {
    NONE,
    REQUEST_SENT,
    REQUEST_RECEIVED,
    FRIENDS
}
