package com.barbermanagerpro.feature.customer.presentation.customerList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel
    @Inject
    constructor(
        repository: CustomerRepository,
    ) : ViewModel() {
        private val _searchText = MutableStateFlow("")
        val searchText: StateFlow<String> = _searchText
        private val customersFlow = repository.getCustomers()

        val state: StateFlow<CustomerListState> =
            combine(customersFlow, _searchText) { list, text ->
                if (text.isBlank()) {
                    CustomerListState(customers = list, isLoading = false)
                } else {
                    val filteredList =
                        list.filter { customer ->
                            customer.doesMatchSearchQuery(text)
                        }
                    CustomerListState(customers = filteredList, isLoading = false)
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CustomerListState(isLoading = true),
            )

        fun onSearchTextChange(text: String) {
            _searchText.value = text
        }

        private fun Customer.doesMatchSearchQuery(query: String): Boolean {
            val matchingCombinations =
                listOf(
                    "${personalInfo.firstName} ${personalInfo.lastName}",
                    personalInfo.firstName,
                    personalInfo.lastName,
                    personalInfo.phone,
                    legacyId ?: "",
                )

            return matchingCombinations.any {
                it.contains(query, ignoreCase = true)
            }
        }
    }
