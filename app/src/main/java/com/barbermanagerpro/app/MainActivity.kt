package com.barbermanagerpro.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.customer.presentation.customerList.CustomerListScreen
import com.barbermanagerpro.ui.theme.BarberManagerProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarberManagerProTheme {
//                AddCustomerScreen(
//                    onNavigateBack = { /* TBD */ },
//                )
                CustomerListScreen(
                    onFabClick = {
                        Toast
                            .makeText(
                                this,
                                getString(R.string.go_to_add_customer_screen),
                                Toast.LENGTH_SHORT,
                            ).show()
                    },
                    onItemClick = { id ->
                        Toast
                            .makeText(
                                this,
                                getString(R.string.click_on_customer_message, id),
                                Toast.LENGTH_SHORT,
                            ).show()
                    },
                )
            }
        }
    }
}
