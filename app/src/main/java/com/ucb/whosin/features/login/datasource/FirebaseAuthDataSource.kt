package com.ucb.whosin.features.login.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun register(email: String, password: String): AuthResult {
        return try {
            Log.d("FirebaseAuth", "🔹 Iniciando registro para: $email")
            Log.d("FirebaseAuth", "🔹 Firebase App: ${firebaseAuth.app.name}")
            // 1. Crear usuario en Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                Log.d("FirebaseAuth", "✅ Usuario creado exitosamente: $userId")


                // 2. Intentar guardar en Firestore
                try {
                    val userData = hashMapOf("email" to email)

                    firestore.collection("users")
                        .document(userId)
                        .set(userData)
                        .await()

                    Log.d("FirebaseAuth", "✅ Datos guardados en Firestore")
                } catch (firestoreError: Exception) {
                    Log.e("FirebaseAuth", "⚠️ Error al guardar en Firestore (usuario creado de todas formas)", firestoreError)
                    // Continúa aunque falle Firestore - el usuario ya está creado en Auth
                }
                // 3. Retornar éxito
                AuthResult.Success(
                    User(
                        uid = userId,
                        email = email
                    )
                )
            } else {
                Log.e("FirebaseAuth", "❌ Usuario nulo después de registro")
                AuthResult.Error("Error al registrar usuario")
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "❌ Error en registro: ${e.javaClass.simpleName}", e)
            Log.e("FirebaseAuth", "❌ Mensaje: ${e.message}")
            AuthResult.Error(e.message ?: "Error desconocido")
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
}