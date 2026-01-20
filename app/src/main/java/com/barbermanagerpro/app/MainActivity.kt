package com.barbermanagerpro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.barbermanagerpro.feature.customer.presentation.add_customer.AddCustomerScreen
import com.barbermanagerpro.ui.theme.BarberManagerProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarberManagerProTheme {
                AddCustomerScreen(
                    onNavigateBack = { /* TBD */ }
                )
            }
        }
    }
}