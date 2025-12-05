package com.ucb.whosin.features.login.domain.vo

import org.junit.Assert.*
import org.junit.Test

class EmailTest {
    @Test
    fun `create with valid email should succeed`() {
        // Given
        val validEmail = "juan@example.com"

        // When
        val result = Email.create(validEmail)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("juan@example.com", result.getOrNull()?.value)
    }

    @Test
    fun `create should normalize email to lowercase`() {
        // Given
        val emailWithUppercase = "JUAN@EXAMPLE.COM"

        // When
        val result = Email.create(emailWithUppercase)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("juan@example.com", result.getOrNull()?.value)
    }

    @Test
    fun `create should trim whitespace`() {
        // Given
        val emailWithSpaces = "  juan@example.com  "

        // When
        val result = Email.create(emailWithSpaces)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("juan@example.com", result.getOrNull()?.value)
    }

    @Test
    fun `create with blank email should fail`() {
        // Given
        val blankEmail = "   "

        // When
        val result = Email.create(blankEmail)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El correo electrónico no puede estar vacío", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with invalid format should fail`() {
        // Given
        val invalidEmails = listOf(
            "notanemail",
            "missing@domain",
            "@nodomain.com",
            "no@domain",
            "spaces in@email.com"
        )

        // When & Then
        invalidEmails.forEach { email ->
            val result = Email.create(email)
            assertTrue("Email '$email' should be invalid", result.isFailure)
            assertEquals("El formato del correo electrónico no es válido", result.exceptionOrNull()?.message)
        }
    }

    @Test
    fun `toString should return email value`() {
        // Given
        val email = Email.create("test@example.com").getOrThrow()

        // When
        val toString = email.toString()

        // Then
        assertEquals("test@example.com", toString)
    }
}