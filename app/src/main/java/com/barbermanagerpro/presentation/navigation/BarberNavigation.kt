package com.barbermanagerpro.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.barbermanagerpro.feature.agenda.presentation.AddAppointmentScreen
import com.barbermanagerpro.feature.customer.presentation.addCustomer.AddCustomerScreen
import com.barbermanagerpro.feature.customer.presentation.customerList.CustomerListScreen
import com.barbermanagerpro.feature.customer.presentation.login.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
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
                onScheduleClick = {
                    navController.navigate(Screens.AddAppointment)
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

        composable<Screens.AddAppointment> {
            AddAppointmentScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
