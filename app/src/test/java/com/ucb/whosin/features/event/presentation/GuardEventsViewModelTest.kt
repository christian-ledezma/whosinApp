
package com.ucb.whosin.features.event.presentation

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ucb.whosin.features.event.domain.model.EventModel
import com.ucb.whosin.features.event.domain.usecase.GetEventsWhereUserIsGuardUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GuardEventsViewModelTest {

    private lateinit var viewModel: GuardEventsViewModel
    private val getEventsWhereUserIsGuardUseCase: GetEventsWhereUserIsGuardUseCase = mockk()
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GuardEventsViewModel(getEventsWhereUserIsGuardUseCase, firebaseAuth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadGuardEvents should show events on success`() = runTest(testDispatcher) {
        // Arrange
        val mockUser: FirebaseUser = mockk()
        val userId = "test-user-id"
        val fakeEvents = listOf(EventModel(eventId = "1", name = "Evento de Prueba", date = Timestamp.now()))
        every { firebaseAuth.currentUser } returns mockUser
        every { mockUser.uid } returns userId
        coEvery { getEventsWhereUserIsGuardUseCase(userId) } returns fakeEvents

        // Act
        viewModel.loadGuardEvents()

        // Permitir que la corrutina finalice
        advanceUntilIdle()

        // Assert: Estado final con los eventos
        val finalState = viewModel.uiState.value
        assertEquals(false, finalState.isLoading)
        assertEquals(fakeEvents, finalState.events)
        assertNull(finalState.errorMessage)
    }

    @Test
    fun `loadGuardEvents should show error message on failure`() = runTest(testDispatcher) {
        // Arrange
        val mockUser: FirebaseUser = mockk()
        val userId = "test-user-id"
        val errorMessage = "Error de red simulado"
        every { firebaseAuth.currentUser } returns mockUser
        every { mockUser.uid } returns userId
        coEvery { getEventsWhereUserIsGuardUseCase(userId) } throws RuntimeException(errorMessage)

        // Act
        viewModel.loadGuardEvents()
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.uiState.value
        assertEquals(false, finalState.isLoading)
        assertEquals(errorMessage, finalState.errorMessage)
        assertEquals(true, finalState.events.isEmpty())
    }

    @Test
    fun `loadGuardEvents should show error when user is not authenticated`() = runTest(testDispatcher) {
        // Arrange
        every { firebaseAuth.currentUser } returns null

        // Act
        viewModel.loadGuardEvents()

        // Assert
        val finalState = viewModel.uiState.value
        assertEquals(false, finalState.isLoading)
        assertNotNull(finalState.errorMessage)
        assertEquals("Usuario no autenticado", finalState.errorMessage)
    }

    @Test
    fun `clearError should reset error message`() = runTest(testDispatcher) {
        // Arrange: Primero, poner el ViewModel en un estado de error
        every { firebaseAuth.currentUser } returns null
        viewModel.loadGuardEvents() // Llamamos para que se establezca el error
        assertNotNull(viewModel.uiState.value.errorMessage) // Pre-verificación

        // Act
        viewModel.clearError()

        // Assert
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
