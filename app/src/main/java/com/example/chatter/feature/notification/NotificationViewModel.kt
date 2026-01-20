package com.example.chatter.feature.notification

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val myId get() = auth.currentUser?.uid ?: ""

    private val _requests = MutableStateFlow<List<FriendRequestUser>>(emptyList())
    val requests: StateFlow<List<FriendRequestUser>> = _requests




    fun observeRequests() {
        db.collection("users")
            .document(myId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val uids = snapshot.get("requestsReceived") as? List<String> ?: emptyList()

                    if (uids.isEmpty()) {
                        _requests.value = emptyList()
                        return@addSnapshotListener
                    }

                    // Fetch all users info
                    db.collection("users")
                        .whereIn("uid", uids)
                        .get()
                        .addOnSuccessListener { userDocs ->
                            val list = userDocs.documents.mapNotNull { doc ->
                                FriendRequestUser(
                                    uid = doc.getString("uid") ?: "",
                                    name = doc.getString("name") ?: "",
                                    username = doc.getString("username") ?: ""
                                )
                            }
                            _requests.value = list
                        }
                }
            }
    }


    fun accept(friendUid: String) {
        val chatId = getChatId(myId, friendUid)

        val batch = db.batch()

        val myRef = db.collection("users").document(myId)
        val friendRef = db.collection("users").document(friendUid)
        val chatRef = db.collection("chats").document(chatId)

        // 1️⃣ Remove requests
        batch.update(myRef, "requestsReceived", FieldValue.arrayRemove(friendUid))
        batch.update(friendRef, "requestsSent", FieldValue.arrayRemove(myId))

        // 2️⃣ Add friends
        batch.update(myRef, "friends", FieldValue.arrayUnion(friendUid))
        batch.update(friendRef, "friends", FieldValue.arrayUnion(myId))

        // 3️⃣ Create chat if not exists
        batch.set(
            chatRef,
            mapOf(
                "chatId" to chatId,
                "members" to listOf(myId, friendUid),
                "createdAt" to FieldValue.serverTimestamp(),
                "lastMessage" to "",
                "lastMessageAt" to FieldValue.serverTimestamp()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        )

        // 4️⃣ Optional: store chat reference for both users
        batch.set(myRef.collection("chats").document(chatId), mapOf("chatId" to chatId))
        batch.set(friendRef.collection("chats").document(chatId), mapOf("chatId" to chatId))

        batch.commit()
    }


    fun reject(uid: String) {
        db.collection("users").document(myId)
            .update("requestsReceived", FieldValue.arrayRemove(uid))
        db.collection("users").document(uid)
            .update("requestsSent", FieldValue.arrayRemove(myId))
    }

    private fun getChatId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_")
    }

}
