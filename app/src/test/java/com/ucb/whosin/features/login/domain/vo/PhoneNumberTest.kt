package com.ucb.whosin.features.login.domain.vo

import org.junit.Assert.*
import org.junit.Test

class PhoneNumberTest {
    @Test
    fun `create with valid phone for Bolivia should succeed`() {
        // Given
        val phone = "12345678"
        val countryCode = "+591"

        // When
        val result = PhoneNumber.create(phone, countryCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("12345678", result.getOrNull()?.value)
    }

    @Test
    fun `create should remove non-digit characters`() {
        // Given
        val phoneWithFormat = "123-456-78"
        val countryCode = "+591"

        // When
        val result = PhoneNumber.create(phoneWithFormat, countryCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("12345678", result.getOrNull()?.value)
    }

    @Test
    fun `create with spaces should normalize`() {
        // Given
        val phoneWithSpaces = "123 456 78"
        val countryCode = "+591"

        // When
        val result = PhoneNumber.create(phoneWithSpaces, countryCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("12345678", result.getOrNull()?.value)
    }

    @Test
    fun `create with blank phone should fail`() {
        // Given
        val blankPhone = "   "
        val countryCode = "+591"

        // When
        val result = PhoneNumber.create(blankPhone, countryCode)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El número de teléfono no puede estar vacío", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with too short phone for Bolivia should fail`() {
        // Given
        val shortPhone = "1234567" // 7 digits, Bolivia needs 8
        val countryCode = "+591"

        // When
        val result = PhoneNumber.create(shortPhone, countryCode)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("al menos 8 dígitos") == true)
    }

    @Test
    fun `create with too long phone for Bolivia should fail`() {
        // Given
        val longPhone = "123456789" // 9 digits, Bolivia max is 8
        val countryCode = "+591"

        // When
        val result = PhoneNumber.create(longPhone, countryCode)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("no puede exceder 8 dígitos") == true)
    }

    @Test
    fun `create with valid phone for US should succeed`() {
        // Given
        val phone = "1234567890" // 10 digits
        val countryCode = "+1"

        // When
        val result = PhoneNumber.create(phone, countryCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("1234567890", result.getOrNull()?.value)
    }

    @Test
    fun `create with valid phone for Mexico should succeed`() {
        // Given
        val phone = "1234567890" // 10 digits
        val countryCode = "+52"

        // When
        val result = PhoneNumber.create(phone, countryCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("1234567890", result.getOrNull()?.value)
    }

    @Test
    fun `create with minimum valid phone for US should succeed`() {
        // Given
        val phone = "1234567" // 7 digits (minimum for +1)
        val countryCode = "+1"

        // When
        val result = PhoneNumber.create(phone, countryCode)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `create with unknown country code should use default range`() {
        // Given
        val phone = "12345678"
        val unknownCountryCode = "+999"

        // When
        val result = PhoneNumber.create(phone, unknownCountryCode)

        // Then
        assertTrue(result.isSuccess) // 8 digits is within default 7-15 range
    }
}