package com.barbermanagerpro.feature.customer.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object CustomerList : Screens

    @Serializable
    data class AddCustomer(
        val customerId: String? = null,
    ) : Screens

    // @Serializable
    // data class EditCustomer(val id: String) : Screen
}
