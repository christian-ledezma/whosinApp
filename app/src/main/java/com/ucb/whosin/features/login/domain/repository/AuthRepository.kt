package com.ucb.whosin.features.login.domain.repository

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun registerUser(email: String, password: String): AuthResult
    suspend fun registerUser(registerData: RegisterData): AuthResult
    suspend fun loginUser(email: String, password: String): AuthResult
    suspend fun saveSession(userId: String, email: String)
    suspend fun clearSession()
    fun isLoggedIn(): Flow<Boolean>
    fun getSession(): Flow<Pair<String?, String?>>
    suspend fun getUserProfile(userId: String): User?
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
}