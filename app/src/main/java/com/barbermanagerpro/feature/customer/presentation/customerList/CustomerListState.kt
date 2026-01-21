package com.barbermanagerpro.feature.customer.presentation.customerList

import com.barbermanagerpro.feature.customer.domain.model.Customer

data class CustomerListState(
    val customers: List<Customer> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)
