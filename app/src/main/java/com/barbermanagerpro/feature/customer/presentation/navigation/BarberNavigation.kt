package com.barbermanagerpro.feature.customer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.barbermanagerpro.feature.customer.presentation.addCustomer.AddCustomerScreen
import com.barbermanagerpro.feature.customer.presentation.customerList.CustomerListScreen
import com.barbermanagerpro.feature.customer.presentation.login.LoginScreen

@Composable
fun BarberNavigation(startDestination: Screens) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Screens.CustomerList> {
            CustomerListScreen(
                onFabClick = {
                    navController.navigate(Screens.AddCustomer(customerId = null))
                },
                onItemClick = { customerId ->
                    navController.navigate(Screens.AddCustomer(customerId = customerId))
                },
                onLogout = {
                    navController.navigate(Screens.Login) {
                        popUpTo(0) { inclusive = true }
                    }
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

        composable<Screens.Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.CustomerList) {
                        popUpTo(Screens.Login) { inclusive = true }
                    }
                },
            )
        }
    }
}
