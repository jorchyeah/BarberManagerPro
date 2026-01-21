package com.barbermanagerpro.feature.customer.presentation.addCustomer

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.customer.domain.model.BirthDate
import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.model.PersonalInfo
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import com.barbermanagerpro.feature.customer.presentation.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
        savedStateHandle: SavedStateHandle,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        private val _state = MutableStateFlow(AddCustomerState())
        val state: StateFlow<AddCustomerState> = _state.asStateFlow()
        private var currentCustomerId: String? = null

        init {
            val args = savedStateHandle.toRoute<Screens.AddCustomer>()
            if (args.customerId != null) {
                currentCustomerId = args.customerId
                loadCustomer(args.customerId)
            }
        }

        private fun loadCustomer(id: String) {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val customer = repository.getCustomerById(id)

                if (customer != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            firstName = customer.personalInfo.firstName,
                            lastName = customer.personalInfo.lastName,
                            phone = customer.personalInfo.phone,
                            birthDay =
                                customer.personalInfo.birthDate
                                    ?.day
                                    ?.toString() ?: "",
                            birthMonth =
                                customer.personalInfo.birthDate
                                    ?.month
                                    ?.toString() ?: "",
                            birthYear =
                                customer.personalInfo.birthDate
                                    ?.year
                                    ?.toString() ?: "",
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = context.getString(R.string.customer_not_found),
                        )
                    }
                }
            }
        }

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

        fun onDeleteClick() {
            if (currentCustomerId == null) return

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                val result = repository.deleteCustomer(currentCustomerId!!)
                if (result.isSuccess) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = context.getString(R.string.customer_deleted_success),
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = context.getString(R.string.delete_error),
                        )
                    }
                }
            }
        }

        fun onSaveClick() {
            _state.update { it.copy(isLoading = true) }
            println(context.getString(R.string.attempt_save_customer, _state.value))
            if (_state.value.firstName.isBlank() || _state.value.phone.isBlank()) {
                _state.update { it.copy(errorMessage = context.getString(R.string.info_required)) }
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val newCustomer =
                    Customer(
                        id = currentCustomerId ?: "",
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
                    val msg =
                        if (currentCustomerId !=
                            null
                        ) {
                            context.getString(R.string.customer_updated_success)
                        } else {
                            context.getString(R.string.customer_saved_success)
                        }
                    _state.update { it.copy(isLoading = false, successMessage = msg) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                result.exceptionOrNull()?.message
                                    ?: context.getString(R.string.unknown_error),
                        )
                    }
                }
            }
        }
    }
