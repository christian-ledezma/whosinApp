package com.ucb.whosin.features.login.datasource

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun register(registerData: RegisterData): AuthResult {
        return try {
            Log.d("FirebaseAuth", "🔹 Iniciando registro para: ${registerData.email}")
            Log.d("FirebaseAuth", "🔹 Firebase App: ${firebaseAuth.app.name}")
            // 1. Crear usuario en Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                registerData.email,
                registerData.password
            ).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                Log.d("FirebaseAuth", "✅ Usuario creado exitosamente: $userId")


                // 2. Crear objeto User con todos los campos
                val user = User(
                    uid = userId,
                    email = registerData.email,
                    name = registerData.name,
                    lastname = registerData.lastname,
                    secondLastname = registerData.secondLastname,
                    phone = registerData.phone,
                    countryCode = registerData.countryCode.code,
                    createdAt = Timestamp.now()
                )

                // 3. Guardar en Firestore
                try {
                    firestore.collection("users")
                        .document(userId)
                        .set(user.toFirestoreMap())
                        .await()

                    Log.d("FirebaseAuth", "✅ Datos guardados en Firestore")
                } catch (firestoreError: Exception) {
                    Log.e("FirebaseAuth", "⚠️ Error al guardar en Firestore (usuario creado de todas formas)", firestoreError)

                }

                AuthResult.Success(user)
            } else {
                Log.e("FirebaseAuth", "❌ Usuario nulo después de registro")
                AuthResult.Error("Error al registrar usuario")
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "❌ Error en registro: ${e.javaClass.simpleName}", e)
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }

    // Mantener compatibilidad con el método antiguo (para login)
    suspend fun register(email: String, password: String): AuthResult {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                val userData = hashMapOf(
                    "email" to email,
                    "createdAt" to Timestamp.now()
                )

                firestore.collection("users")
                    .document(userId)
                    .set(userData)
                    .await()

                AuthResult.Success(User(uid = userId, email = email))
            } else {
                AuthResult.Error("Error al registrar usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(mapFirebaseError(e.message))
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Obtener datos del usuario desde Firestore
                val userDoc = firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                if (userDoc.exists()) {
                    AuthResult.Success(
                        User(
                            uid = firebaseUser.uid,
                            email = userDoc.getString("email") ?: email
                        )
                    )
                } else {
                    AuthResult.Error("Usuario no encontrado en la base de datos")
                }
            } else {
                AuthResult.Error("Error al iniciar sesión")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (userDoc.exists()) {
                User.fromFirestoreMap(userId, userDoc.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error al obtener perfil", e)
            null
        }
    }

    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            val updateData = mapOf(
                "name" to user.name,
                "lastname" to user.lastname,
                "secondLastname" to user.secondLastname,
                "phone" to user.phone,
                "countryCode" to user.countryCode
            )

            firestore.collection("users")
                .document(user.uid)
                .update(updateData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Error al actualizar perfil", e)
            Result.failure(e)
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val email = user.email
                ?: return Result.failure(Exception("Email no disponible"))

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