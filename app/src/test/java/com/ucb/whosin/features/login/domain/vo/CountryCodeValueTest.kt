package com.ucb.whosin.features.login.domain.vo

import com.ucb.whosin.features.login.domain.model.CountryCode
import org.junit.Assert.*
import org.junit.Test

class CountryCodeValueTest {
    @Test
    fun `create with valid country code should succeed`() {
        // Given
        val validCode = "+591"

        // When
        val result = CountryCodeValue.create(validCode)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("+591", result.getOrNull()?.value)
    }

    @Test
    fun `create with blank code should fail`() {
        // Given
        val blankCode = "   "

        // When
        val result = CountryCodeValue.create(blankCode)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El código de país no puede estar vacío", result.exceptionOrNull()?.message)
    }

    @Test
    fun `create with invalid code should fail`() {
        // Given
        val invalidCode = "+999"

        // When
        val result = CountryCodeValue.create(invalidCode)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Código de país no válido: +999", result.exceptionOrNull()?.message)
    }

    @Test
    fun `fromEnum should create valid CountryCodeValue`() {
        // Given
        val countryCodeEnum = CountryCode.BO

        // When
        val result = CountryCodeValue.fromEnum(countryCodeEnum)

        // Then
        assertEquals("+591", result.value)
    }

    @Test
    fun `all CountryCode enum values should be valid`() {
        // Given
        val allCountryCodes = CountryCode.entries

        // When & Then
        allCountryCodes.forEach { countryCode ->
            val result = CountryCodeValue.create(countryCode.code)
            assertTrue("Country code ${countryCode.code} should be valid", result.isSuccess)
        }
    }

    @Test
    fun `toString should return code value`() {
        // Given
        val countryCode = CountryCodeValue.create("+591").getOrThrow()

        // When
        val toString = countryCode.toString()

        // Then
        assertEquals("+591", toString)
    }
}