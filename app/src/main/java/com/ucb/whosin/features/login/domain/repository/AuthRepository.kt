package com.ucb.whosin.features.login.domain.repository

import com.ucb.whosin.features.login.domain.model.AuthResult

interface AuthRepository {
    suspend fun registerUser(email: String, password: String): AuthResult
    suspend fun loginUser(email: String, password: String): AuthResult
}