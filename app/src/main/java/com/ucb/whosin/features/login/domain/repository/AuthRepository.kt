package com.ucb.whosin.features.login.domain.repository

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.model.vo.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    @Deprecated("Usar loginUser con value objects")
    suspend fun registerUser(email: String, password: String): AuthResult

    suspend fun registerUser(registerData: RegisterData): AuthResult

    suspend fun loginUser(email: Email, password: Password): AuthResult

    suspend fun saveSession(userId: UserId, email: Email)
    suspend fun clearSession()
    fun isLoggedIn(): Flow<Boolean>
    fun getSession(): Flow<Pair<String?, String?>>

    suspend fun getUserProfile(userId: UserId): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun changePassword(currentPassword: Password, newPassword: Password): Result<Unit>
}