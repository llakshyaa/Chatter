package com.example.chatter.feature.home

data class FriendUiModel(
    val uid: String,
    val name: String,
    val username: String,
    val chatId: String,
    val lastMessage: String = "",
    val lastMessageAt: Long? = null
)
