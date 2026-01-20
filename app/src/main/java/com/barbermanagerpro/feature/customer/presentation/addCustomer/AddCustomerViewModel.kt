package com.barbermanagerpro.feature.customer.presentation.addCustomer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barbermanagerpro.feature.customer.domain.model.BirthDate
import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.model.PersonalInfo
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel
    @Inject
    constructor(
        private val repository: CustomerRepository,
    ) : ViewModel() {
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
            if (_state.value.firstName.isBlank() || _state.value.phone.isBlank()) {
                _state.update { it.copy(errorMessage = "Nombre y teléfono obligatorios") }
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val newCustomer =
                    Customer(
                        shopId = "",
                        personalInfo =
                            PersonalInfo(
                                firstName = _state.value.firstName,
                                lastName = _state.value.lastName,
                                phone = _state.value.phone,
                                birthDate =
                                    if (_state.value.birthDay.isNotBlank()) {
                                        BirthDate(
                                            _state.value.birthDay.toIntOrNull() ?: 1,
                                            _state.value.birthMonth.toIntOrNull() ?: 1,
                                            _state.value.birthYear.toIntOrNull() ?: 2000,
                                        )
                                    } else {
                                        null
                                    },
                            ),
                    )

                val result = repository.saveCustomer(newCustomer)

                if (result.isSuccess) {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido",
                        )
                    }
                }
            }
        }
    }
