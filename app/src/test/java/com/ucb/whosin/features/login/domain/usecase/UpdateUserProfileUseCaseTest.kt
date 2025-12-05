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

class UpdateUserProfileUseCaseTest {

    @Mock
    private lateinit var mockRepository: AuthRepository

    private lateinit var useCase: UpdateUserProfileUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = UpdateUserProfileUseCase(mockRepository)
    }

    @Test
    fun `invoke with valid user should succeed`() = runTest {
        // Given
        val user = createMockUser()

        `when`(mockRepository.updateUserProfile(user))
            .thenReturn(Result.success(Unit))

        // When
        val result = useCase(user)

        // Then
        assertTrue(result.isSuccess)
        verify(mockRepository).updateUserProfile(user)
    }

    @Test
    fun `invoke with repository error should return error`() = runTest {
        // Given
        val user = createMockUser()

        `when`(mockRepository.updateUserProfile(user))
            .thenReturn(Result.failure(Exception("Error al actualizar")))

        // When
        val result = useCase(user)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error al actualizar", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should delegate validation to value objects`() = runTest {
        // Given - User ya validado por value objects
        val user = createMockUser()

        `when`(mockRepository.updateUserProfile(user))
            .thenReturn(Result.success(Unit))

        // When
        val result = useCase(user)

        // Then
        assertTrue(result.isSuccess)
        // Las validaciones ya ocurrieron al crear los value objects
        verify(mockRepository).updateUserProfile(user)
    }

    private fun createMockUser(): User {
        return User.create(
            uid = UserId.create("user123").getOrThrow(),
            email = Email.create("juan@example.com").getOrThrow(),
            name = PersonName.create("JUAN").getOrThrow(),
            lastname = PersonName.create("PÉREZ").getOrThrow(),
            secondLastname = OptionalPersonName.create("GARCÍA").getOrThrow(),
            phoneNumber = PhoneNumber.create("12345678", "+591").getOrThrow(),
            countryCode = CountryCodeValue.create("+591").getOrThrow()
        )
    }
}