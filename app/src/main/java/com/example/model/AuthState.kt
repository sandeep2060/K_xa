package com.example.model

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Suspended(val reason: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
