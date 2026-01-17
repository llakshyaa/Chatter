package com.example.chatter.feature.chat

data class Message(
    val id: String ="",
   val text: String ="",
    val senderId: String ="",
//    val receiverId: String="",
    val createdAt: Long = System.currentTimeMillis(),

    val senderName: String="",
    val senderImage: String?="",
    val imageUrl: String?=""



)
