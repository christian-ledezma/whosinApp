package com.ucb.whosin.features.login.domain.usecase

import com.ucb.whosin.features.login.domain.repository.AuthRepository
import com.ucb.whosin.features.login.domain.vo.Password
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any


class ChangePasswordUseCaseTest {

    @Mock
    private lateinit var mockRepository: AuthRepository

    private lateinit var useCase: ChangePasswordUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = ChangePasswordUseCase(mockRepository)
    }

    @Test
    fun `invoke with valid passwords should succeed`() = runTest {
        // Given
        val currentPassword = "123456"
        val newPassword = "newpass123"
        val confirmPassword = "newpass123"

        `when`(mockRepository.changePassword(any(), any()))
            .thenReturn(Result.success(Unit))

        // When
        val result = useCase(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isSuccess)
        verify(mockRepository).changePassword(any(), any())
    }

    @Test
    fun `invoke with blank current password should fail`() = runTest {
        // Given
        val currentPassword = ""
        val newPassword = "newpass123"
        val confirmPassword = "newpass123"

        // When
        val result = useCase(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La contraseña no puede estar vacía", result.exceptionOrNull()?.message)
        verify(mockRepository, never()).changePassword(any(), any())
    }

    @Test
    fun `invoke with short new password should fail`() = runTest {
        // Given
        val currentPassword = "123456"
        val newPassword = "12345"
        val confirmPassword = "12345"

        // When
        val result = useCase(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La contraseña debe tener al menos 6 caracteres", result.exceptionOrNull()?.message)
        verify(mockRepository, never()).changePassword(any(), any())
    }

    @Test
    fun `invoke with mismatched passwords should fail`() = runTest {
        // Given
        val currentPassword = "123456"
        val newPassword = "newpass123"
        val confirmPassword = "different123"

        // When
        val result = useCase(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Las contraseñas no coinciden", result.exceptionOrNull()?.message)
        verify(mockRepository, never()).changePassword(any(), any())
    }

    @Test
    fun `invoke with same current and new password should fail`() = runTest {
        // Given
        val currentPassword = "123456"
        val newPassword = "123456"
        val confirmPassword = "123456"

        // When
        val result = useCase(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("La nueva contraseña debe ser diferente a la actual", result.exceptionOrNull()?.message)
        verify(mockRepository, never()).changePassword(any(), any())
    }

    @Test
    fun `invoke with repository error should return error`() = runTest {
        // Given
        val currentPassword = "123456"
        val newPassword = "newpass123"
        val confirmPassword = "newpass123"

        `when`(mockRepository.changePassword(any(), any()))
            .thenReturn(Result.failure(Exception("Error de autenticación")))

        // When
        val result = useCase(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error de autenticación", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should validate confirm password format`() = runTest {
        // Given
        val currentPassword = "123456"
        val newPassword = "newpass123"
        val confirmPassword = "short" // Too short

        // When
        val result = useCase(currentPassword, newPassword, confirmPassword)

        // Then
        assertTrue(result.isFailure)
        // Falla porque confirmPassword es muy corto (< 6 caracteres)
        verify(mockRepository, never()).changePassword(any(), any())
    }
}