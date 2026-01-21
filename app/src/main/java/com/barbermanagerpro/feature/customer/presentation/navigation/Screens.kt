package com.barbermanagerpro.feature.customer.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object CustomerList : Screens

    @Serializable
    data object AddCustomer : Screens

    // @Serializable
    // data class EditCustomer(val id: String) : Screen
}
