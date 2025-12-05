package com.ucb.whosin.features.login.domain.vo

import org.junit.Assert.*
import org.junit.Test

class OptionalPersonNameTest {
    @Test
    fun `create with null should succeed`() {
        // When
        val result = OptionalPersonName.create(null)

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull()?.value)
    }

    @Test
    fun `create with blank string should succeed`() {
        // When
        val result = OptionalPersonName.create("   ")

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull()?.value)
    }

    @Test
    fun `create with valid name should normalize to uppercase`() {
        // Given
        val name = "garcía"

        // When
        val result = OptionalPersonName.create(name)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("GARCÍA", result.getOrNull()?.value)
    }

    @Test
    fun `create with compound name should succeed`() {
        // Given
        val compoundName = "de la cruz"

        // When
        val result = OptionalPersonName.create(compoundName)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("DE LA CRUZ", result.getOrNull()?.value)
    }

    @Test
    fun `create with too short name should fail`() {
        // Given
        val shortName = "g"

        // When
        val result = OptionalPersonName.create(shortName)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El apellido debe tener al menos 2 caracteres", result.exceptionOrNull()?.message)
    }

    @Test
    fun `orEmpty should return empty string when value is null`() {
        // Given
        val optional = OptionalPersonName.create(null).getOrThrow()

        // When
        val result = optional.orEmpty()

        // Then
        assertEquals("", result)
    }

    @Test
    fun `orEmpty should return value when not null`() {
        // Given
        val optional = OptionalPersonName.create("garcía").getOrThrow()

        // When
        val result = optional.orEmpty()

        // Then
        assertEquals("GARCÍA", result)
    }

    @Test
    fun `toString should return empty string when null`() {
        // Given
        val optional = OptionalPersonName.create(null).getOrThrow()

        // When
        val toString = optional.toString()

        // Then
        assertEquals("", toString)
    }
}