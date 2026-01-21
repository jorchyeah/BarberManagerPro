package com.barbermanagerpro.feature.customer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.barbermanagerpro.feature.customer.presentation.addCustomer.AddCustomerScreen
import com.barbermanagerpro.feature.customer.presentation.customerList.CustomerListScreen

@Composable
fun BarberNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.CustomerList,
    ) {
        composable<Screens.CustomerList> {
            CustomerListScreen(
                onFabClick = {
                    navController.navigate(Screens.AddCustomer(customerId = null))
                },
                onItemClick = { customerId ->
                    navController.navigate(Screens.AddCustomer(customerId = customerId))
                },
            )
        }

        composable<Screens.AddCustomer> { backStackEntry ->
            val args = backStackEntry.toRoute<Screens.AddCustomer>()
            AddCustomerScreen(
                customerId = args.customerId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
