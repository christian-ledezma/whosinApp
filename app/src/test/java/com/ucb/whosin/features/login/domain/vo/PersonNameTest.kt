package com.ucb.whosin.features.login.domain.vo

import org.junit.Assert.*
import org.junit.Test

class PersonNameTest {
    @Test
    fun `create with valid name should succeed and normalize to uppercase`() {
        // Given
        val name = "juan"

        // When
        val result = PersonName.create(name)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("JUAN", result.getOrNull()?.value)
    }

    @Test
    fun `create with mixed case should normalize to uppercase`() {
        // Given
        val name = "JuAn"

        // When
        val result = PersonName.create(name)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("JUAN", result.getOrNull()?.value)
    }

    @Test
    fun `create with compound name should allow single space`() {
        // Given
        val compoundName = "maria jose"

        // When
        val result = PersonName.create(compoundName)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("MARIA JOSE", result.getOrNull()?.value)
    }

    @Test
    fun `create should trim and normalize multiple spaces to single space`() {
        // Given
        val nameWithSpaces = "  maria   jose  "

        // When
        val result = PersonName.create(nameWithSpaces)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("MARIA JOSE", result.getOrNull()?.value)
    }

    @Test
    fun `create with accented characters should succeed`() {
        // Given
        val accentedName = "josé maría"

        // When
        val result = PersonName.create(accentedName)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("JOSÉ MARÍA", result.getOrNull()?.value)
    }

    @Test
    fun `create with ñ should succeed`() {
        // Given
        val nameWithÑ = "niño"

        // When
        val result = PersonName.create(nameWithÑ)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("NIÑO", result.getOrNull()?.value)
    }

    @Test
    fun `create with blank name should fail`() {
        // Given
        val blankName = "   "

        // When
        val result = PersonName.create(blankName)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre no puede estar vacío", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with too short name should fail`() {
        // Given
        val shortName = "J"

        // When
        val result = PersonName.create(shortName)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre debe tener al menos 2 caracteres", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with too long name should fail`() {
        // Given
        val longName = "a".repeat(51)

        // When
        val result = PersonName.create(longName)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre no puede exceder 50 caracteres", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with numbers should fail`() {
        // Given
        val nameWithNumbers = "juan123"

        // When
        val result = PersonName.create(nameWithNumbers)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre solo puede contener letras y un espacio para nombres compuestos", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with special characters should fail`() {
        // Given
        val nameWithSpecialChars = "juan@"

        // When
        val result = PersonName.create(nameWithSpecialChars)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El nombre solo puede contener letras y un espacio para nombres compuestos", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with three words should succeed`() {
        val nameWithThreeWords = "juan pedro jose"
        val result = PersonName.create(nameWithThreeWords)

        assertTrue(result.isSuccess)
        assertEquals("JUAN PEDRO JOSE", result.getOrNull()?.value)
    }

    @Test
    fun `create with custom field name should use it in error message`() {
        // Given
        val blankName = ""

        // When
        val result = PersonName.create(blankName, "apellido")

        // Then
        assertTrue(result.isFailure)
        assertEquals("El apellido no puede estar vacío", result.exceptionOrNull()?.message)
    }
}