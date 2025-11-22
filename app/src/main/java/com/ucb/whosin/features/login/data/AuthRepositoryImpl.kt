package com.ucb.whosin.features.login.data

import com.ucb.whosin.features.login.datasource.FirebaseAuthDataSource
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun registerUser(email: String, password: String): AuthResult {
        return dataSource.register(email, password)
    }

    override suspend fun registerUser(registerData: RegisterData): AuthResult {
        return dataSource.register(registerData)
    }

    override suspend fun loginUser(email: String, password: String): AuthResult {
        return dataSource.login(email, password)
    }

    override suspend fun saveSession(userId: String, email: String) {
        sessionManager.saveSession(userId, email)
    }

    override suspend fun clearSession() {
        sessionManager.clearSession()
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return sessionManager.isLoggedIn()
    }

    override fun getSession(): Flow<Pair<String?, String?>> {
        return sessionManager.getSession()
    }

    override suspend fun getUserProfile(userId: String): User? {
        return dataSource.getUserProfile(userId)
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return dataSource.updateUserProfile(user)
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return dataSource.changePassword(currentPassword, newPassword)
    }
}