package com.example.chatter.feature.chat

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(): ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val db = Firebase.database


//    val channelRef = db.getReference("channels")

    fun sendMessage(channelID: String, messageText: String) {
         val message=Message(
              db.getReference("messages").child(channelID).push().key ?: UUID.randomUUID().toString(),
             messageText,
             Firebase.auth.currentUser?.uid ?: "",
             System.currentTimeMillis(),
             Firebase.auth.currentUser?.displayName ?: "",
             null,
             null
         )
         db.getReference("messages").child(channelID).push().setValue(message)
    }



    fun listenForMessages(channelID: String) {
        db.getReference("messages").child(channelID).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)
                        message?.let { list.add(it) }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
    }
}
