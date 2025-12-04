package com.ucb.whosin.features.login.datasource

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.model.vo.UserId
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSourceImp(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseAuthDataSource {
    override suspend fun register(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult.Error("Error al crear usuario")

            val userMap = mapOf(
                "email" to email,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(userMap)
                .await()

            val userResult = User.fromFirestoreMap(firebaseUser.uid, userMap)
            if (userResult.isSuccess) {
                AuthResult.Success(userResult.getOrThrow())
            } else {
                AuthResult.Error("Error al crear perfil de usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }

    // Mantener compatibilidad con el metodo antiguo (para login)
    override suspend fun register(registerData: RegisterData): AuthResult {
        return try {
            // Crear usuario en Firebase Auth
            val result = auth.createUserWithEmailAndPassword(
                registerData.email.value,
                registerData.password.value
            ).await()

            val firebaseUser = result.user ?: return AuthResult.Error("Error al crear usuario")

            // Crear perfil completo en Firestore
            val userMap = mapOf(
                "email" to registerData.email.value,
                "name" to registerData.name.value,
                "lastname" to registerData.lastname.value,
                "secondLastname" to registerData.secondLastname.value,
                "phone" to registerData.phoneNumber.value,
                "countryCode" to registerData.countryCode.value,
                "createdAt" to com.google.firebase.Timestamp.now()
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(userMap)
                .await()

            // Convertir a User domain model
            val userResult = User.fromFirestoreMap(firebaseUser.uid, userMap)
            if (userResult.isSuccess) {
                AuthResult.Success(userResult.getOrThrow())
            } else {
                AuthResult.Error("Error al crear perfil de usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return AuthResult.Error("Usuario no encontrado")

            val userResult = getUserProfile(firebaseUser.uid)

            if (userResult.isSuccess) {
                    AuthResult.Success(userResult.getOrThrow())
            } else {
                AuthResult.Error("Error al obtener perfil de usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    override suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (!document.exists()) {
                return Result.failure(Exception("Usuario no encontrado"))
            }

            val data = document.data ?: return Result.failure(Exception("Datos de usuario vacíos"))

            // Convertir a User usando el factory method que valida
            User.fromFirestoreMap(userId, data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(user.uid.value)
                .update(user.toFirestoreMap())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error al actualizar perfil", e)
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(
                Exception("Usuario no autenticado"))

            val email = user.email ?: return Result.failure(
                Exception("Email no disponible"))

            // Re-autenticar al usuario
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()

            // Cambiar contraseña
            user.updatePassword(newPassword).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error al cambiar contraseña", e)
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null -> "Error desconocido"
            message.contains("email-already-in-use", ignoreCase = true) ->
                "Este correo ya está registrado"
            message.contains("invalid-email", ignoreCase = true) ->
                "El formato del correo no es válido"
            message.contains("weak-password", ignoreCase = true) ->
                "La contraseña es muy débil"
            message.contains("wrong-password", ignoreCase = true) ->
                "Contraseña incorrecta"
            message.contains("user-not-found", ignoreCase = true) ->
                "Usuario no encontrado"
            message.contains("too-many-requests", ignoreCase = true) ->
                "Demasiados intentos. Intenta más tarde"
            message.contains("network", ignoreCase = true) ->
                "Error de conexión. Verifica tu internet"
            else -> message
        }
    }
}