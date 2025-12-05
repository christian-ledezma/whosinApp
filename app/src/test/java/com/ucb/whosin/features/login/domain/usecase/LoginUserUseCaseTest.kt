package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.model.AuthResult
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

class LoginUserUseCaseTest {
    @Mock
    private lateinit var mockRepository: AuthRepository

    private lateinit var useCase: LoginUserUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = LoginUserUseCase(mockRepository)
    }

    @Test
    fun `invoke with valid credentials should return success`() = runTest {
        // Given
        val email = "juan@example.com"
        val password = "123456"
        val mockUser = createMockUser()

        `when`(mockRepository.loginUser(any(Email::class.java), any(Password::class.java)))
            .thenReturn(AuthResult.Success(mockUser))

        // When
        val result = useCase(email, password)

        // Then
        assertTrue(result is AuthResult.Success)
        assertEquals(mockUser, (result as AuthResult.Success).user)
        verify(mockRepository).loginUser(any(Email::class.java), any(Password::class.java))
    }

    @Test
    fun `invoke with invalid email format should return error`() = runTest {
        // Given
        val invalidEmail = "notanemail"
        val password = "123456"

        // When
        val result = useCase(invalidEmail, password)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("El formato del correo electrónico no es válido", (result as AuthResult.Error).message)
        verify(mockRepository, never()).loginUser(any(Email::class.java), any(Password::class.java))
    }

    @Test
    fun `invoke with blank email should return error`() = runTest {
        // Given
        val blankEmail = "   "
        val password = "123456"

        // When
        val result = useCase(blankEmail, password)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("El correo electrónico no puede estar vacío", (result as AuthResult.Error).message)
        verify(mockRepository, never()).loginUser(any(Email::class.java), any(Password::class.java))
    }

    @Test
    fun `invoke with short password should return error`() = runTest {
        // Given
        val email = "juan@example.com"
        val shortPassword = "12345"

        // When
        val result = useCase(email, shortPassword)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("La contraseña debe tener al menos 6 caracteres", (result as AuthResult.Error).message)
        verify(mockRepository, never()).loginUser(any(Email::class.java), any(Password::class.java))
    }

    @Test
    fun `invoke with blank password should return error`() = runTest {
        // Given
        val email = "juan@example.com"
        val blankPassword = ""

        // When
        val result = useCase(email, blankPassword)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("La contraseña no puede estar vacía", (result as AuthResult.Error).message)
        verify(mockRepository, never()).loginUser(any(Email::class.java), any(Password::class.java))
    }

    @Test
    fun `invoke should normalize email to lowercase`() = runTest {
        // Given
        val emailUppercase = "JUAN@EXAMPLE.COM"
        val password = "123456"
        val mockUser = createMockUser()

        `when`(mockRepository.loginUser(any(Email::class.java), any(Password::class.java)))
            .thenReturn(AuthResult.Success(mockUser))

        // When
        val result = useCase(emailUppercase, password)

        // Then
        assertTrue(result is AuthResult.Success)
        verify(mockRepository).loginUser(argThat { email ->
            email.value == "juan@example.com"
        }, any(Password::class.java))
    }

    @Test
    fun `invoke with wrong credentials should return repository error`() = runTest {
        // Given
        val email = "juan@example.com"
        val password = "wrongpassword"

        `when`(mockRepository.loginUser(any(Email::class.java), any(Password::class.java)))
            .thenReturn(AuthResult.Error("Credenciales incorrectas"))

        // When
        val result = useCase(email, password)

        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Credenciales incorrectas", (result as AuthResult.Error).message)
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