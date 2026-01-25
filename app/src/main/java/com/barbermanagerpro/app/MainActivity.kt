package com.barbermanagerpro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.barbermanagerpro.feature.customer.presentation.navigation.BarberNavigation
import com.barbermanagerpro.feature.customer.presentation.navigation.Screens
import com.barbermanagerpro.ui.theme.BarberManagerProTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startScreen =
            if (auth.currentUser != null) {
                Screens.CustomerList
            } else {
                Screens.Login
            }

        setContent {
            BarberManagerProTheme {
                BarberNavigation(startDestination = startScreen)
            }
        }
    }
}
