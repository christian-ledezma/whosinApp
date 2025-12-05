package com.ucb.whosin.features.login.domain.model

import com.google.firebase.Timestamp
import com.ucb.whosin.features.login.domain.vo.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests de integración para User
 * Prueba la conversión desde/hacia Firestore
 */

class UserIntegrationTest {

    @Test
    fun `create User and convert to Firestore map should preserve all data`() {
        // Given
        val user = User.create(
            uid = UserId.create("user123").getOrThrow(),
            email = Email.create("juan@example.com").getOrThrow(),
            name = PersonName.create("JUAN").getOrThrow(),
            lastname = PersonName.create("PÉREZ").getOrThrow(),
            secondLastname = OptionalPersonName.create("GARCÍA").getOrThrow(),
            phoneNumber = PhoneNumber.create("12345678", "+591").getOrThrow(),
            countryCode = CountryCodeValue.create("+591").getOrThrow(),
            createdAt = Timestamp.now()
        )

        // When
        val firestoreMap = user.toFirestoreMap()

        // Then
        assertEquals("juan@example.com", firestoreMap["email"])
        assertEquals("JUAN", firestoreMap["name"])
        assertEquals("PÉREZ", firestoreMap["lastname"])
        assertEquals("GARCÍA", firestoreMap["secondLastname"])
        assertEquals("12345678", firestoreMap["phone"])
        assertEquals("+591", firestoreMap["countryCode"])
        assertNotNull(firestoreMap["createdAt"])
    }

    @Test
    fun `convert from Firestore map to User should validate all fields`() {
        // Given
        val uid = "user123"
        val firestoreMap = mapOf(
            "email" to "juan@example.com",
            "name" to "JUAN",
            "lastname" to "PÉREZ",
            "secondLastname" to "GARCÍA",
            "phone" to "12345678",
            "countryCode" to "+591",
            "createdAt" to Timestamp.now()
        )

        // When
        val result = User.fromFirestoreMap(uid, firestoreMap)

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrThrow()
        assertEquals("user123", user.uid.value)
        assertEquals("juan@example.com", user.email.value)
        assertEquals("JUAN", user.name.value)
        assertEquals("PÉREZ", user.lastname.value)
        assertEquals("GARCÍA", user.secondLastname.value)
        assertEquals("12345678", user.phoneNumber.value)
        assertEquals("+591", user.countryCode.value)
    }

    @Test
    fun `convert from Firestore with null secondLastname should succeed`() {
        // Given
        val uid = "user123"
        val firestoreMap = mapOf(
            "email" to "juan@example.com",
            "name" to "JUAN",
            "lastname" to "PÉREZ",
            "secondLastname" to null,
            "phone" to "12345678",
            "countryCode" to "+591"
        )

        // When
        val result = User.fromFirestoreMap(uid, firestoreMap)

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow().secondLastname.value)
    }

    @Test
    fun `convert from Firestore with invalid email should fail`() {
        // Given
        val uid = "user123"
        val firestoreMap = mapOf(
            "email" to "notanemail",
            "name" to "JUAN",
            "lastname" to "PÉREZ",
            "phone" to "12345678",
            "countryCode" to "+591"
        )

        // When
        val result = User.fromFirestoreMap(uid, firestoreMap)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("correo") == true)
    }

    @Test
    fun `convert from Firestore with invalid phone for country should fail`() {
        // Given
        val uid = "user123"
        val firestoreMap = mapOf(
            "email" to "juan@example.com",
            "name" to "JUAN",
            "lastname" to "PÉREZ",
            "phone" to "123", // Muy corto
            "countryCode" to "+591"
        )

        // When
        val result = User.fromFirestoreMap(uid, firestoreMap)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("teléfono") == true)
    }

    @Test
    fun `fullName should combine all name parts correctly`() {
        // Given
        val user = User.create(
            uid = UserId.create("user123").getOrThrow(),
            email = Email.create("juan@example.com").getOrThrow(),
            name = PersonName.create("JUAN PEDRO").getOrThrow(),
            lastname = PersonName.create("PÉREZ").getOrThrow(),
            secondLastname = OptionalPersonName.create("GARCÍA").getOrThrow(),
            phoneNumber = PhoneNumber.create("12345678", "+591").getOrThrow(),
            countryCode = CountryCodeValue.create("+591").getOrThrow()
        )

        // When
        val fullName = user.fullName()

        // Then
        assertEquals("JUAN PEDRO PÉREZ GARCÍA", fullName)
    }

    @Test
    fun `fullName without secondLastname should work correctly`() {
        // Given
        val user = User.create(
            uid = UserId.create("user123").getOrThrow(),
            email = Email.create("juan@example.com").getOrThrow(),
            name = PersonName.create("JUAN").getOrThrow(),
            lastname = PersonName.create("PÉREZ").getOrThrow(),
            secondLastname = OptionalPersonName.create(null).getOrThrow(),
            phoneNumber = PhoneNumber.create("12345678", "+591").getOrThrow(),
            countryCode = CountryCodeValue.create("+591").getOrThrow()
        )

        // When
        val fullName = user.fullName()

        // Then
        assertEquals("JUAN PÉREZ", fullName)
    }

    @Test
    fun `fullPhone should combine country code and phone number`() {
        // Given
        val user = User.create(
            uid = UserId.create("user123").getOrThrow(),
            email = Email.create("juan@example.com").getOrThrow(),
            name = PersonName.create("JUAN").getOrThrow(),
            lastname = PersonName.create("PÉREZ").getOrThrow(),
            secondLastname = OptionalPersonName.create(null).getOrThrow(),
            phoneNumber = PhoneNumber.create("12345678", "+591").getOrThrow(),
            countryCode = CountryCodeValue.create("+591").getOrThrow()
        )

        // When
        val fullPhone = user.fullPhone()

        // Then
        assertEquals("+591 12345678", fullPhone)
    }

    @Test
    fun `round trip conversion User to Firestore to User should preserve data`() {
        // Given - User original
        val originalUser = User.create(
            uid = UserId.create("user123").getOrThrow(),
            email = Email.create("juan@example.com").getOrThrow(),
            name = PersonName.create("JUAN").getOrThrow(),
            lastname = PersonName.create("PÉREZ").getOrThrow(),
            secondLastname = OptionalPersonName.create("GARCÍA").getOrThrow(),
            phoneNumber = PhoneNumber.create("12345678", "+591").getOrThrow(),
            countryCode = CountryCodeValue.create("+591").getOrThrow(),
            createdAt = Timestamp.now()
        )

        // When - Convertir a Firestore y de vuelta
        val firestoreMap = originalUser.toFirestoreMap()
        val reconstructedUser = User.fromFirestoreMap("user123", firestoreMap).getOrThrow()

        // Then - Verificar que los datos son iguales
        assertEquals(originalUser.uid.value, reconstructedUser.uid.value)
        assertEquals(originalUser.email.value, reconstructedUser.email.value)
        assertEquals(originalUser.name.value, reconstructedUser.name.value)
        assertEquals(originalUser.lastname.value, reconstructedUser.lastname.value)
        assertEquals(originalUser.secondLastname.value, reconstructedUser.secondLastname.value)
        assertEquals(originalUser.phoneNumber.value, reconstructedUser.phoneNumber.value)
        assertEquals(originalUser.countryCode.value, reconstructedUser.countryCode.value)
    }
}