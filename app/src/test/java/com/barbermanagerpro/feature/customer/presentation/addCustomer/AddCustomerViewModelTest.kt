package com.barbermanagerpro.feature.customer.presentation.addCustomer

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.barbermanagerpro.core.MainDispatcherRule
import com.barbermanagerpro.feature.customer.data.repository.FakeCustomerRepository
import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.model.PersonalInfo
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AddCustomerViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddCustomerViewModel
    private lateinit var fakeRepository: FakeCustomerRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var context: Context

    @Before
    fun setUp() {
        fakeRepository = FakeCustomerRepository()
        savedStateHandle = SavedStateHandle()
        context = mockk(relaxed = true)
        every { context.getString(any()) } returns "Cliente eliminado correctamente"

        viewModel =
            AddCustomerViewModel(
                repository = fakeRepository,
                savedStateHandle = savedStateHandle,
                context = context,
            )
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.state.value

        assertEquals("", state.firstName)
        assertEquals("", state.phone)
        assertFalse(state.isLoading)
    }

    @Test
    fun `onFirstNameChange updates state correctly`() {
        // Arrange
        val newName = "Carlos"

        // Act
        viewModel.onFirstNameChange(newName)

        // Assert
        assertEquals(newName, viewModel.state.value.firstName)
    }

    @Test
    fun `onPhoneChange allows numbers`() {
        val number = "5512345678"
        viewModel.onPhoneChange(number)
        assertEquals(number, viewModel.state.value.phone)
    }

    @Test
    fun `onPhoneChange ignores letters`() {
        // Arrange
        viewModel.onPhoneChange("")

        // Act
        viewModel.onPhoneChange("55abc")

        // Assert
        assertEquals("", viewModel.state.value.phone)
    }

    @Test
    fun `onSaveClick sets loading to true`() {
        // Act
        viewModel.onSaveClick()

        // Assert
        assertTrue(viewModel.state.value.isLoading)
    }

    @Test
    fun `onSaveClick saves customer to repository`() =
        runTest {
            // Arrange
            viewModel.onFirstNameChange("Juan")
            viewModel.onLastNameChange("Perez")
            viewModel.onPhoneChange("5551234567")

            // Act
            viewModel.onSaveClick()
            advanceUntilIdle()

            // Assert
            val customers = fakeRepository.getCustomers()
        }

    @Test
    fun `init loads customer data when ID is provided (Edit Mode)`() =
        runTest {
            val existingCustomer =
                Customer(
                    id = "id_123",
                    shopId = "shop_1",
                    personalInfo = PersonalInfo("Ana", "Lopez", "999888777", null),
                )
            fakeRepository.saveCustomer(existingCustomer)

            val editStateHandle = SavedStateHandle(mapOf("customerId" to "id_123"))

            val editViewModel =
                AddCustomerViewModel(
                    repository = fakeRepository,
                    savedStateHandle = editStateHandle,
                    context = context,
                )

            advanceUntilIdle()

            // Assert
            assertEquals("Ana", editViewModel.state.value.firstName)
            assertEquals("999888777", editViewModel.state.value.phone)
        }
}
