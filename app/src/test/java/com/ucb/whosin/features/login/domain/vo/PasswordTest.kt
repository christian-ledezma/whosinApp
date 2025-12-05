package com.ucb.whosin.features.login.domain.vo

import org.junit.Assert.*
import org.junit.Test

class PasswordTest {
    @Test
    fun `create with valid password should succeed`() {
        // Given
        val validPassword = "123456"

        // When
        val result = Password.create(validPassword)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("123456", result.getOrNull()?.value)
    }

    @Test
    fun `create with password longer than minimum should succeed`() {
        // Given
        val longPassword = "mySecurePassword123!"

        // When
        val result = Password.create(longPassword)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("mySecurePassword123!", result.getOrNull()?.value)
    }

    @Test
    fun `create with blank password should fail`() {
        // Given
        val blankPassword = "   "

        // When
        val result = Password.create(blankPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La contraseña no puede estar vacía", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with short password should fail`() {
        // Given
        val shortPassword = "12345"

        // When
        val result = Password.create(shortPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La contraseña debe tener al menos 6 caracteres", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validate with same passwords should fail`() {
        // Given
        val password = Password.create("123456").getOrThrow()
        val samePassword = Password.create("123456").getOrThrow()

        // When
        val result = Password.validate(password, samePassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La nueva contraseña debe ser diferente a la actual", result.exceptionOrNull()?.message)
    }

    @Test
    fun `validate with different passwords should succeed`() {
        // Given
        val currentPassword = Password.create("123456").getOrThrow()
        val newPassword = Password.create("654321").getOrThrow()

        // When
        val result = Password.validate(currentPassword, newPassword)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `toString should not expose password value`() {
        // Given
        val password = Password.create("123456").getOrThrow()

        // When
        val toString = password.toString()

        // Then
        assertEquals("***", toString)
    }
}