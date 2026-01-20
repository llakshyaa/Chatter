package com.example.chatter.feature.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val myUid: String
        get() = auth.currentUser?.uid ?: ""

    private val _friends = MutableStateFlow<List<FriendUiModel>>(emptyList())
    val friends: StateFlow<List<FriendUiModel>> = _friends

    /** OBSERVE FRIEND LIST */
    fun observeFriends() {
        if (myUid.isEmpty()) return

        db.collection("users")
            .document(myUid)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null) return@addSnapshotListener

                val friendUids =
                    snapshot.get("friends") as? List<String> ?: emptyList()

                if (friendUids.isEmpty()) {
                    _friends.value = emptyList()
                    return@addSnapshotListener
                }

                // Fetch friend profiles
                db.collection("users")
                    .whereIn("uid", friendUids)
                    .addSnapshotListener { usersSnap, _ ->

                        if (usersSnap == null) return@addSnapshotListener

                        val list = usersSnap.documents.mapNotNull { doc ->
                            val uid = doc.getString("uid") ?: return@mapNotNull null

                            val chatId = getChatId(myUid, uid)

                            FriendUiModel(
                                uid = uid,
                                name = doc.getString("name") ?: "",
                                username = doc.getString("username") ?: "",
                                chatId = chatId
                            )
                        }

                        _friends.value = list
                    }
            }
    }

    private fun getChatId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_")
    }
}
