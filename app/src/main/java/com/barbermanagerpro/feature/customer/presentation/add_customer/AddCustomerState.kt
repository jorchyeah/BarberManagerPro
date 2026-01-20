package com.barbermanagerpro.feature.customer.presentation.add_customer

data class AddCustomerState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val birthDay: String = "",
    val birthMonth: String = "",
    val birthYear: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)