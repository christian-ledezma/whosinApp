
package com.ucb.whosin.features.Guest.presentation

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ucb.whosin.features.Guest.domain.model.Guest
import com.ucb.whosin.features.Guest.domain.model.GuestResult
import com.ucb.whosin.features.Guest.domain.usecase.AddGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.DeleteGuestUseCase
import com.ucb.whosin.features.Guest.domain.usecase.GetGuestsUseCase
import com.ucb.whosin.features.Guest.domain.usecase.UpdateGuestUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GuestListViewModelTest {

    private lateinit var viewModel: GuestListViewModel
    private val getGuestsUseCase: GetGuestsUseCase = mockk()
    private val updateGuestUseCase: UpdateGuestUseCase = mockk()
    private val deleteGuestUseCase: DeleteGuestUseCase = mockk()
    private val addGuestUseCase: AddGuestUseCase = mockk()
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val eventId = "test-event-id"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("eventId") } returns eventId

        // Mockear un usuario autenticado por defecto en la mayoría de las pruebas
        val mockUser: FirebaseUser = mockk()
        every { firebaseAuth.currentUser } returns mockUser

        viewModel = GuestListViewModel(
            getGuestsUseCase = getGuestsUseCase,
            updateGuestUseCase = updateGuestUseCase,
            deleteGuestUseCase = deleteGuestUseCase,
            addGuestUseCase = addGuestUseCase,
            firebaseAuth = firebaseAuth,
            savedStateHandle = savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadGuests should update state with guests on success`() = runTest(testDispatcher) {
        val guests = listOf(Guest(guestId = "1", name = "Alice"))
        coEvery { getGuestsUseCase(eventId) } returns GuestResult.SuccessList(guests)

        viewModel.loadGuests()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(guests, state.guests)
        assertEquals(guests, state.filteredGuests)
    }

    @Test
    fun `loadGuests should update state with error on failure`() = runTest(testDispatcher) {
        val errorMessage = "Failed to load guests"
        coEvery { getGuestsUseCase(eventId) } returns GuestResult.Error(errorMessage)

        viewModel.loadGuests()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.errorMessage)
    }

    @Test
    fun `addGuest should reload guests on success`() = runTest(testDispatcher) {
        val newGuest = Guest(name = "Charlie", plusOnesAllowed = 1, groupSize = 2)
        coEvery { addGuestUseCase(eventId, newGuest) } returns GuestResult.Success(newGuest)
        // Hacemos que la recarga de invitados devuelva una lista que incluya al nuevo
        coEvery { getGuestsUseCase(eventId) } returns GuestResult.SuccessList(listOf(newGuest))

        viewModel.addGuest("Charlie", 1)
        advanceUntilIdle()

        coVerify { getGuestsUseCase(eventId) } // Verificar que se recargó la lista
        assertEquals(listOf(newGuest), viewModel.uiState.value.guests)
    }

    @Test
    fun `deleteGuest should set deleteSuccess flag on success`() = runTest(testDispatcher) {
        val guestId = "guest-to-delete"
        coEvery { deleteGuestUseCase(eventId, guestId) } returns GuestResult.Success(Guest(guestId = guestId))

        viewModel.deleteGuest(guestId)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.deleteSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `updateGuest should set updateSuccess flag on success`() = runTest(testDispatcher) {
        val guest = Guest(guestId = "1", name = "Alice", plusOnesAllowed = 0, groupSize = 1)
        val updatedGuest = guest.copy(name = "Alicia", plusOnesAllowed = 1, groupSize = 2)
        coEvery { getGuestsUseCase(eventId) } returns GuestResult.SuccessList(listOf(guest))
        coEvery { updateGuestUseCase(eventId, "1", updatedGuest) } returns GuestResult.Success(updatedGuest)

        // Cargar datos iniciales
        viewModel.loadGuests()
        advanceUntilIdle()

        // Act
        viewModel.updateGuest("1", "Alicia", 1)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.updateSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onSearchQueryChange should filter guests`() = runTest(testDispatcher) {
        val guests = listOf(
            Guest(guestId = "1", name = "Alice Smith"),
            Guest(guestId = "2", name = "Bob Johnson")
        )
        coEvery { getGuestsUseCase(eventId) } returns GuestResult.SuccessList(guests)
        viewModel.loadGuests()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("Smith")

        val state = viewModel.uiState.value
        assertEquals("Smith", state.searchQuery)
        assertEquals(1, state.filteredGuests.size)
        assertEquals("Alice Smith", state.filteredGuests[0].name)
    }

    @Test
    fun `clearError should set errorMessage to null`() {
        // Arrange: Poner el ViewModel en un estado de error
        coEvery { getGuestsUseCase(eventId) } returns GuestResult.Error("Error inicial")
        runTest(testDispatcher) {
            viewModel.loadGuests()
            advanceUntilIdle()
        }
        assertNotNull(viewModel.uiState.value.errorMessage)

        // Act
        viewModel.clearError()

        // Assert
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `resetSuccessFlags should set all success flags to false`() {
        // Arrange: Poner el ViewModel en un estado de éxito
        runTest(testDispatcher) {
            coEvery { deleteGuestUseCase(any(), any()) } returns GuestResult.Success(Guest())
            viewModel.deleteGuest("some-id")
            advanceUntilIdle()
        }
        assertTrue(viewModel.uiState.value.deleteSuccess)

        // Act
        viewModel.resetSuccessFlags()

        // Assert
        assertFalse(viewModel.uiState.value.deleteSuccess)
        assertFalse(viewModel.uiState.value.updateSuccess)
    }
}
