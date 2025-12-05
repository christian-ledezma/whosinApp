package com.ucb.whosin.features.login.domain.vo

import org.junit.Assert.*
import org.junit.Test

class UserIdTest {
    @Test
    fun `create with valid id should succeed`() {
        // Given
        val validId = "user123abc"

        // When
        val result = UserId.create(validId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("user123abc", result.getOrNull()?.value)
    }

    @Test
    fun `create with Firebase UID format should succeed`() {
        // Given
        val firebaseUid = "xYz123AbC456DeF789"

        // When
        val result = UserId.create(firebaseUid)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("xYz123AbC456DeF789", result.getOrNull()?.value)
    }

    @Test
    fun `create with blank id should fail`() {
        // Given
        val blankId = "   "

        // When
        val result = UserId.create(blankId)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El ID de usuario no puede estar vacío", result.exceptionOrNull()?.message)
    }

    @Test
    fun `toString should return id value`() {
        // Given
        val userId = UserId.create("user123").getOrThrow()

        // When
        val toString = userId.toString()

        // Then
        assertEquals("user123", toString)
    }
}