package com.ucb.whosin.features.login.domain.usecase

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

class GetUserProfileUseCaseTest {

    @Mock
    private lateinit var mockRepository: AuthRepository

    private lateinit var useCase: GetUserProfileUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetUserProfileUseCase(mockRepository)
    }

    @Test
    fun `invoke with valid userId should return user`() = runTest {
        // Given
        val userId = "user123"
        val mockUser = createMockUser()

        `when`(mockRepository.getUserProfile(any()))
            .thenReturn(Result.success(mockUser))

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockUser, result.getOrNull())
        verify(mockRepository).getUserProfile(any())
    }

    @Test
    fun `invoke with blank userId should return error`() = runTest {
        // Given
        val blankUserId = "   "

        // When
        val result = useCase(blankUserId)

        // Then
        assertTrue(result.isFailure)
        assertEquals("El ID de usuario no puede estar vacío", result.exceptionOrNull()?.message)
        verify(mockRepository, never()).getUserProfile(any())
    }

    @Test
    fun `invoke with repository error should return error`() = runTest {
        // Given
        val userId = "user123"

        `when`(mockRepository.getUserProfile(any()))
            .thenReturn(Result.failure(Exception("Usuario no encontrado")))

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Usuario no encontrado", result.exceptionOrNull()?.message)
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