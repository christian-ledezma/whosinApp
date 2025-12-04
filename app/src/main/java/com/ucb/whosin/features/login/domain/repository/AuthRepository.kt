package com.ucb.whosin.features.login.domain.repository

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.model.vo.*
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Métodos simplificados para login básico (deprecated, mantener por compatibilidad)
    @Deprecated("Usar loginUser con value objects")
    suspend fun registerUser(email: String, password: String): AuthResult

    // Registro completo con value objects
    suspend fun registerUser(registerData: RegisterData): AuthResult

    // Login con value objects
    suspend fun loginUser(email: Email, password: Password): AuthResult

    // Sesión (se mantienen como String por ser primitivos de storage)
    suspend fun saveSession(userId: UserId, email: Email)
    suspend fun clearSession()
    fun isLoggedIn(): Flow<Boolean>
    fun getSession(): Flow<Pair<String?, String?>>

    // Perfil de usuario
    suspend fun getUserProfile(userId: UserId): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun changePassword(currentPassword: Password, newPassword: Password): Result<Unit>
}