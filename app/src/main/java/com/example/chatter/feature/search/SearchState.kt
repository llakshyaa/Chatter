package com.example.chatter.feature.search

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    object NotFound : SearchState()
    data class Success(val user: UserSearchResult) : SearchState()
    data class Error(val message: String) : SearchState()
}
