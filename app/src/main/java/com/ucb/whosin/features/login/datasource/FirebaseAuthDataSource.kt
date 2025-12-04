package com.ucb.whosin.features.login.datasource

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User

interface FirebaseAuthDataSource {
    suspend fun register(email: String, password: String): AuthResult
    suspend fun register(registerData: RegisterData): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    suspend fun getUserProfile(userId: String): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
}