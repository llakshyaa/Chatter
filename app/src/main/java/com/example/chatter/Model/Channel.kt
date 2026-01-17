package com.example.chatter.Model

data class Channel (
    val id: String="",
    val name: String="",
    val members: Map<String, Boolean> = emptyMap(),
    val createdAt: Long= System.currentTimeMillis()
)