package com.barbermanagerpro.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screens {
    @Serializable
    data object CustomerList : Screens

    @Serializable
    data class AddCustomer(
        val customerId: String? = null,
    ) : Screens

    @Serializable
    data object Login : Screens

    @Serializable
    data object AddAppointment : Screens
}
