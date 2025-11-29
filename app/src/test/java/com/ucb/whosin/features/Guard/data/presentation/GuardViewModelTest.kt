
package com.ucb.whosin.features.Guard.data.presentation

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ucb.whosin.features.Guard.data.model.Guest
import com.ucb.whosin.features.Guard.data.repository.GuardRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GuardViewModelTest {

    private lateinit var guardViewModel: GuardViewModel
    private val guardRepository: GuardRepository = mockk(relaxed = true)
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val guestsFlow = MutableStateFlow<List<Guest>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("eventId") } returns "testEventId"
        every { guardRepository.getGuests("testEventId") } returns guestsFlow
        guardViewModel = GuardViewModel(guardRepository, firebaseAuth, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChange should update searchQuery`() = runTest {
        val query = "John"
        guardViewModel.onSearchQueryChange(query)
        assertEquals(query, guardViewModel.searchQuery.value)
    }

    @Test
    fun `filteredGuests should return all guests when query is blank`() = runTest {
        val guests = listOf(
            Guest(guestId = "1", name = "Alice", checkedIn = false),
            Guest(guestId = "2", name = "Bob", checkedIn = true)
        )
        guestsFlow.value = guests
        advanceUntilIdle()

        guardViewModel.onSearchQueryChange("")
        advanceUntilIdle()

        assertEquals(guests, guardViewModel.filteredGuests.value)
    }

    @Test
    fun `filteredGuests should return filtered guests when query is not blank`() = runTest {
        val guests = listOf(
            Guest(guestId = "1", name = "Alice Smith", checkedIn = false),
            Guest(guestId = "2", name = "Bob Johnson", checkedIn = true),
            Guest(guestId = "3", name = "Charlie Smith", checkedIn = false)
        )
        guestsFlow.value = guests
        advanceUntilIdle()

        guardViewModel.onSearchQueryChange("Smith")
        advanceUntilIdle()

        val filtered = guardViewModel.filteredGuests.value
        assertEquals(2, filtered.size)
        assertEquals("Alice Smith", filtered[0].name)
        assertEquals("Charlie Smith", filtered[1].name)
    }

    @Test
    fun `checkIn should call repository`() = runTest {
        val guestId = "guest123"
        val guardId = "guard456"
        val mockUser: FirebaseUser = mockk()
        every { firebaseAuth.currentUser } returns mockUser
        every { mockUser.uid } returns guardId

        guardViewModel.checkIn(guestId)
        advanceUntilIdle()

        coVerify { guardRepository.checkInGuest("testEventId", guestId, guardId) }
    }

    @Test
    fun `stats should be calculated correctly`() = runTest {
        val guests = listOf(
            Guest(guestId = "1", name = "Alice", checkedIn = true),
            Guest(guestId = "2", name = "Bob", checkedIn = false),
            Guest(guestId = "3", name = "Charlie", checkedIn = true),
            Guest(guestId = "4", name = "David", checkedIn = false),
            Guest(guestId = "5", name = "Eve", checkedIn = true)
        )
        guestsFlow.value = guests
        advanceUntilIdle()

        val stats = guardViewModel.stats.value
        assertEquals(3, stats.checkedIn)
        assertEquals(5, stats.total)
    }
}
