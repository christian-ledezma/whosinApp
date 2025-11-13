package com.ucb.whosin.features.login.domain.repository

import com.ucb.whosin.features.login.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(email: String, password: String): AuthResult
    suspend fun loginUser(email: String, password: String): AuthResult
    suspend fun saveSession(userId: String, email: String)
    suspend fun clearSession()
    fun isLoggedIn(): Flow<Boolean>
    fun getSession(): Flow<Pair<String?, String?>>
}