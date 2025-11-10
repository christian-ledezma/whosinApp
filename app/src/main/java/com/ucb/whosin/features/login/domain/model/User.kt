package com.ucb.whosin.features.login.domain.model

data class User (
    val uid: String = "",
    val email: String = ""
)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}