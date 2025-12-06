package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.AuthResult
import com.ucb.whosin.features.login.domain.model.CountryCode
import com.ucb.whosin.features.login.domain.model.RegisterData
import com.ucb.whosin.features.login.domain.model.User
import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.vo.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class RegisterUserUseCaseTest {
    @Mock
    private lateinit var mockRepository: AuthRepository

    private lateinit var useCase: RegisterUserUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = RegisterUserUseCase(mockRepository)
    }

    @Test
    fun `invoke with valid data should return success`() = runTest {
        // Given
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val secondLastname = "García"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        val mockUser = createMockUser()
        `when`(mockRepository.registerUser(any()))
            .thenReturn(AuthResult.Success(mockUser))

        // When
        val result = useCase(email, password, name, lastname, secondLastname, phone, countryCode)

        // Then
        assertTrue(result is AuthResult.Success)
        verify(mockRepository).registerUser(any())
    }

    @Test
    fun `invoke with invalid email should return error`() = runTest {
        // Given
        val invalidEmail = "notanemail"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        // When
        val result = useCase(invalidEmail, password, name, lastname, null, phone, countryCode)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("El formato del correo electrónico no es válido", (result as AuthResult.Error).message)
        verify(mockRepository, never()).registerUser(any())
    }

    @Test
    fun `invoke with short password should return error`() = runTest {
        // Given
        val email = "juan@example.com"
        val shortPassword = "12345"
        val name = "Juan"
        val lastname = "Pérez"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        // When
        val result = useCase(email, shortPassword, name, lastname, null, phone, countryCode)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("La contraseña debe tener al menos 6 caracteres", (result as AuthResult.Error).message)
        verify(mockRepository, never()).registerUser(any())
    }

    @Test
    fun `invoke with blank name should return error`() = runTest {
        // Given
        val email = "juan@example.com"
        val password = "123456"
        val blankName = "   "
        val lastname = "Pérez"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        // When
        val result = useCase(email, password, blankName, lastname, null, phone, countryCode)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("El nombre no puede estar vacío", (result as AuthResult.Error).message)
        verify(mockRepository, never()).registerUser(any())
    }

    @Test
    fun `invoke with blank lastname should return error`() = runTest {
        // Given
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val blankLastname = ""
        val phone = "12345678"
        val countryCode = CountryCode.BO

        // When
        val result = useCase(email, password, name, blankLastname, null, phone, countryCode)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("El apellido paterno no puede estar vacío", (result as AuthResult.Error).message)
            verify(mockRepository, never()).registerUser(any())
    }

    @Test
    fun `invoke with invalid phone should return error`() = runTest {
        // Given
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val shortPhone = "123" // Too short for Bolivia
        val countryCode = CountryCode.BO

        // When
        val result = useCase(email, password, name, lastname, null, shortPhone, countryCode)

        // Then
        assertTrue(result is AuthResult.Error)
        assertTrue((result as AuthResult.Error).message.contains("al menos"))
        verify(mockRepository, never()).registerUser(any())
    }

    @Test
    fun `invoke with null secondLastname should succeed`() = runTest {
        // Given
        val email = "juan@example.com"
        val password = "123456"
        val name = "Juan"
        val lastname = "Pérez"
        val phone = "12345678"
        val countryCode = CountryCode.BO

        val mockUser = createMockUser()
        `when`(mockRepository.registerUser(any()))
            .thenReturn(AuthResult.Success(mockUser))

        // When
        val result = useCase(email, password, name, lastname, null, phone, countryCode)

        // Then
        assertTrue(result is AuthResult.Success)
        verify(mockRepository).registerUser(any())
    }


    private fun createMockUser(): User {
        return User.create(
            uid = UserId.create("user123").getOrThrow(),
            email = Email.create("juan@example.com").getOrThrow(),
            name = PersonName.create("JUAN").getOrThrow(),
            lastname = PersonName.create("PÉREZ").getOrThrow(),
            secondLastname = OptionalPersonName.create(null).getOrThrow(),
            phoneNumber = PhoneNumber.create("12345678", "+591").getOrThrow(),
            countryCode = CountryCodeValue.create("+591").getOrThrow()
        )
    }
}