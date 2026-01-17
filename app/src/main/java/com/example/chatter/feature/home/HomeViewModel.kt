package com.example.chatter.feature.home

import androidx.lifecycle.ViewModel
import com.example.chatter.Model.Channel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val firebaseDatabase = Firebase.database

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    fun findUserByEmail(email: String, onUserFound: (String?) -> Unit) {
        firebaseDatabase.getReference("users")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userUid = snapshot.children.firstOrNull()?.key
                    onUserFound(userUid)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    onUserFound(null)
                }
            })
    }

    fun fetchUserChannels() {
        // Get the current user's UID
        val userUid = Firebase.auth.currentUser?.uid ?: return

        firebaseDatabase.getReference("channels")
            .orderByChild("members/$userUid")
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { data ->
                        data.getValue(Channel::class.java)?.copy(id = data.key ?: "")
                    }
                    _channels.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // TODO: Handle error
                }
            })
    }

    fun addChannel(name: String, memberUids: List<String>) {
        val ref = firebaseDatabase.getReference("channels").push()
        val currentUserUid = Firebase.auth.currentUser?.uid ?: return

        // Combine current user's UID and the friend's UID
        val allMembers = memberUids.toMutableList()
        if (!allMembers.contains(currentUserUid)) {
            allMembers.add(currentUserUid)
        }

        val membersMap = allMembers.associateWith { true }

        val channel = Channel(
            id = ref.key ?: "",
            name = name,
            members = membersMap
        )
        ref.setValue(channel)
    }
}