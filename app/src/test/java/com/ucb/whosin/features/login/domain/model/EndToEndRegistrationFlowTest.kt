package com.ucb.whosin.features.login.domain.model

import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import org.junit.Assert.*
import org.junit.Test

/**
 * Test de flujo completo End-to-End
 * Simula el flujo de registro desde UI hasta persistencia
 */
class EndToEndRegistrationFlowTest {

    @Test
    fun `complete registration flow from UI input to User model`() {
        // Given - Datos como vienen de la UI
        val uiEmail = "  JUAN@EXAMPLE.COM  "
        val uiPassword = "myPassword123"
        val uiName = "juan pedro"
        val uiLastname = "pérez garcía"
        val uiSecondLastname = "  lópez  "
        val uiPhone = "1234-5678"
        val uiCountryCode = CountryCode.BO

        // When - Crear RegisterData (simula lo que hace el UseCase)
        val registerDataResult = RegisterData.create(
            email = uiEmail,
            password = uiPassword,
            name = uiName,
            lastname = uiLastname,
            secondLastname = uiSecondLastname,
            phone = uiPhone,
            countryCode = uiCountryCode
        )

        // Then - Validar que se creó correctamente
        assertTrue("RegisterData should be created successfully", registerDataResult.isSuccess)
        val registerData = registerDataResult.getOrThrow()

        // Verificar normalizaciones
        assertEquals("juan@example.com", registerData.email.value)
        assertEquals("JUAN PEDRO", registerData.name.value)
        assertEquals("PÉREZ GARCÍA", registerData.lastname.value)
        assertEquals("LÓPEZ", registerData.secondLastname.value)
        assertEquals("12345678", registerData.phoneNumber.value)
        assertEquals("+591", registerData.countryCode.value)

        // When - Simular guardado en Firestore y recuperación
        val firestoreMap = mapOf(
            "email" to registerData.email.value,
            "name" to registerData.name.value,
            "lastname" to registerData.lastname.value,
            "secondLastname" to registerData.secondLastname.value,
            "phone" to registerData.phoneNumber.value,
            "countryCode" to registerData.countryCode.value
        )

        val userResult = User.fromFirestoreMap("generatedUserId123", firestoreMap)

        // Then - Verificar que el User se recuperó correctamente
        assertTrue("User should be created from Firestore", userResult.isSuccess)
        val user = userResult.getOrThrow()

        assertEquals("generatedUserId123", user.uid.value)
        assertEquals("juan@example.com", user.email.value)
        assertEquals("JUAN PEDRO", user.name.value)
        assertEquals("PÉREZ GARCÍA", user.lastname.value)
        assertEquals("LÓPEZ", user.secondLastname.value)
        assertEquals("12345678", user.phoneNumber.value)
        assertEquals("+591", user.countryCode.value)

        // Verificar métodos de utilidad
        assertEquals("JUAN PEDRO PÉREZ GARCÍA LÓPEZ", user.fullName())
        assertEquals("+591 12345678", user.fullPhone())
    }

    @Test
    fun `registration flow should fail fast with invalid email`() {
        // Given - Email inválido
        val invalidEmail = "notanemail"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        // When
        val result = RegisterData.create(
            invalidEmail, password, name, lastname, null, phone, countryCode
        )

        // Then - Debe fallar en el primer campo inválido (email)
        assertTrue(result.isFailure)
        val errorMessage = result.exceptionOrNull()?.message
        assertTrue(errorMessage?.contains("correo") == true)
    }

    @Test
    fun `registration flow with minimal data should work`() {
        // Given - Datos mínimos (sin segundo apellido)
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        // When
        val result = RegisterData.create(
            email, password, name, lastname, null, phone, countryCode
        )

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow().secondLastname.value)
    }

    @Test
    fun `registration flow should validate phone according to selected country`() {
        // Test 1: Bolivia - 8 dígitos exactos
        val boResult = RegisterData.create(
            "juan@example.com", "123456", "Juan", "Pérez", null,
            "12345678", CountryCode.BO
        )
        assertTrue("Bolivia phone should be valid", boResult.isSuccess)

        // Test 2: México - 10 dígitos exactos
        val mxResult = RegisterData.create(
            "juan@example.com", "123456", "Juan", "Pérez", null,
            "1234567890", CountryCode.MX
        )
        assertTrue("Mexico phone should be valid", mxResult.isSuccess)

        // Test 3: US - mínimo 7 dígitos
        val usResult = RegisterData.create(
            "juan@example.com", "123456", "Juan", "Pérez", null,
            "1234567", CountryCode.US
        )
        assertTrue("US phone should be valid", usResult.isSuccess)
    }
}