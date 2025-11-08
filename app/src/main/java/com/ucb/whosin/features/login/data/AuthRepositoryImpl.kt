package com.ucb.whosin.features.login.data

import com.ucb.whosin.features.login.datasource.FirebaseAuthDataSource
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {
    override suspend fun registerUser(email: String, password: String): AuthResult {
        return dataSource.register(email, password)
    }

    override suspend fun loginUser(email: String, password: String): AuthResult {
        return dataSource.login(email, password)
    }
}