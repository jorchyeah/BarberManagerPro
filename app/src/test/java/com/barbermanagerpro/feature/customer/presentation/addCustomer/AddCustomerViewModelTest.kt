package com.barbermanagerpro.feature.customer.presentation.addCustomer

import com.barbermanagerpro.core.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AddCustomerViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModel = AddCustomerViewModel()

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
}
