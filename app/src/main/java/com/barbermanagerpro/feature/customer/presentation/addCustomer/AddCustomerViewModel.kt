package com.barbermanagerpro.feature.customer.presentation.addCustomer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel
    @Inject
    constructor() : ViewModel() {
        private val _state = MutableStateFlow(AddCustomerState())
        val state: StateFlow<AddCustomerState> = _state.asStateFlow()

        fun onFirstNameChange(newValue: String) {
            _state.update { it.copy(firstName = newValue) }
        }

        fun onLastNameChange(newValue: String) {
            _state.update { it.copy(lastName = newValue) }
        }

        fun onPhoneChange(newValue: String) {
            if (newValue.all { it.isDigit() }) {
                _state.update { it.copy(phone = newValue) }
            }
        }

        fun onBirthDayChange(newValue: String) {
            _state.update { it.copy(birthDay = newValue) }
        }

        fun onBirthMonthChange(newValue: String) {
            _state.update { it.copy(birthMonth = newValue) }
        }

        fun onBirthYearChange(newValue: String) {
            _state.update { it.copy(birthYear = newValue) }
        }

        fun onSaveClick() {
            _state.update { it.copy(isLoading = true) }
            println("Intentando guardar: ${_state.value}")
        }
    }
