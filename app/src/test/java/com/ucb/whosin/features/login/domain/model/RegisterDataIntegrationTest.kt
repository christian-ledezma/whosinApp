package com.ucb.whosin.features.login.domain.model

import com.ucb.whosin.features.login.domain.vo.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests de integración para RegisterData
 * Prueba el flujo completo de creación y validación
 */
class RegisterDataIntegrationTest {

    @Test
    fun `create RegisterData with all valid fields should succeed`() {
        // Given
        val email = "juan@example.com"
        val password = "123456"
        val name = "juan pedro"
        val lastname = "pérez"
        val secondLastname = "garcía"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        // When
        val result = RegisterData.create(
            email, password, name, lastname, secondLastname, phone, countryCode
        )

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrThrow()

        // Verificar normalización
        assertEquals("juan@example.com", data.email.value)
        assertEquals("JUAN PEDRO", data.name.value)
        assertEquals("PÉREZ", data.lastname.value)
        assertEquals("GARCÍA", data.secondLastname.value)
        assertEquals("12345678", data.phoneNumber.value)
        assertEquals("+591", data.countryCode.value)
    }

    @Test
    fun `create RegisterData should validate all fields in order`() {
        // Given - Email inválido (debe fallar primero)
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

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("correo") == true)
    }

    @Test
    fun `create RegisterData with null secondLastname should succeed`() {
        // Given
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
    fun `create RegisterData should validate phone according to country`() {
        // Given - Teléfono válido para Bolivia (8 dígitos)
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val phoneBO = "12345678"
        val countryCodeBO = CountryCode.BO

        // When
        val result = RegisterData.create(
            email, password, name, lastname, null, phoneBO, countryCodeBO
        )

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `create RegisterData with phone too short for country should fail`() {
        // Given - Teléfono muy corto para Bolivia (necesita 8)
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val shortPhone = "1234567" // Solo 7 dígitos
        val countryCode = CountryCode.BO

        // When
        val result = RegisterData.create(
            email, password, name, lastname, null, shortPhone, countryCode
        )

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("al menos 8") == true)
    }

    @Test
    fun `create RegisterData should normalize phone by removing special characters`() {
        // Given - Teléfono con formato
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val phoneWithFormat = "1234-5678"
        val countryCode = CountryCode.BO

        // When
        val result = RegisterData.create(
            email, password, name, lastname, null, phoneWithFormat, countryCode
        )

        // Then
        assertTrue(result.isSuccess)
        assertEquals("12345678", result.getOrThrow().phoneNumber.value)
    }

    @Test
    fun `create RegisterData with different countries should validate accordingly`() {
        // Test para México (10 dígitos)
        val resultMX = RegisterData.create(
            "juan@example.com", "123456", "Juan", "Pérez", null,
            "1234567890", CountryCode.MX
        )
        assertTrue(resultMX.isSuccess)

        // Test para US (7-10 dígitos)
        val resultUS = RegisterData.create(
            "juan@example.com", "123456", "Juan", "Pérez", null,
            "1234567", CountryCode.US
        )
        assertTrue(resultUS.isSuccess)
    }
}