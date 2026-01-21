package com.barbermanagerpro.feature.customer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.customer.presentation.addCustomer.AddCustomerScreen
import com.barbermanagerpro.feature.customer.presentation.customerList.CustomerListScreen

@Composable
fun BarberNavigation() {
    val navController = rememberNavController()
    val customerClickedMessage = stringResource(R.string.click_on_customer_message)

    NavHost(
        navController = navController,
        startDestination = Screens.CustomerList,
    ) {
        composable<Screens.CustomerList> {
            CustomerListScreen(
                onFabClick = {
                    navController.navigate(Screens.AddCustomer)
                },
                onItemClick = { customerId ->
                    println(customerClickedMessage.format(customerId))
                },
            )
        }

        composable<Screens.AddCustomer> {
            AddCustomerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
