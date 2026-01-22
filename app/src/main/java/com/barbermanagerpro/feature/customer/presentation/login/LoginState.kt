package com.barbermanagerpro.feature.customer.presentation.login

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val emailInput: String = "",
    val passwordInput: String = "",
)
