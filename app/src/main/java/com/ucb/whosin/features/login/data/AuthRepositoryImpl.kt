package com.ucb.whosin.features.login.data

import com.ucb.whosin.features.login.datasource.FirebaseAuthDataSourceImp
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.model.vo.*
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSourceImp,
    private val sessionManager: SessionManager
) : AuthRepository {

    @Deprecated("Usar registerUser con RegisterData")
    override suspend fun registerUser(email: String, password: String): AuthResult {
        return dataSource.register(email, password)
    }

    override suspend fun registerUser(registerData: RegisterData): AuthResult {
        return dataSource.register(registerData)
    }

    override suspend fun loginUser(email: Email, password: Password): AuthResult {
        return dataSource.login(email.value, password.value)
    }

    override suspend fun saveSession(userId: UserId, email: Email) {
        sessionManager.saveSession(userId.value, email.value)
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

    override suspend fun getUserProfile(userId: UserId): Result<User> {
        return dataSource.getUserProfile(userId.value)
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return dataSource.updateUserProfile(user)
    }

    override suspend fun changePassword(
        currentPassword: Password,
        newPassword: Password
    ): Result<Unit> {
        return dataSource.changePassword(currentPassword.value, newPassword.value)
    }
}