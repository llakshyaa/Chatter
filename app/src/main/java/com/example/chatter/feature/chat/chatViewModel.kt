package com.example.chatter.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    var chatId: String = ""
    var friendName: String? = null

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    fun listenForMessages(chatId: String) {
        this.chatId = chatId
        viewModelScope.launch {
            db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(ChatMessage::class.java)
                    } ?: emptyList()
                    _messages.value = list
                }
        }
    }

    fun sendMessage(messageText: String) {
        if (chatId.isEmpty()) return

        val message = ChatMessage(
            senderId = currentUserId,
            text = messageText,
            timestamp = Timestamp.now()
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
    }
}
