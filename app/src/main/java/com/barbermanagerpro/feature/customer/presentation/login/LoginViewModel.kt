package com.barbermanagerpro.feature.customer.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.customer.domain.model.User
import com.barbermanagerpro.feature.customer.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val repository: AuthRepository,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        private val _state = MutableStateFlow(LoginState())
        val state: StateFlow<LoginState> = _state.asStateFlow()

        fun onSignInSuccess(idToken: String?) {
            if (idToken == null) {
                onSignInError(context.getString(R.string.null_token_error))
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }

                val result = repository.signInWithGoogle(idToken)

                if (result.isSuccess) {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    onSignInError(
                        result.exceptionOrNull()?.message
                            ?: context.getString(R.string.unknown_error),
                    )
                }
            }
        }

        fun onSignInError(errorMessage: String) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                )
            }
        }

        fun onGoogleSignInError(statusCode: Int) {
            val message = context.getString(R.string.google_error, statusCode)
            onSignInError(message)
        }

        fun onSignInCancelled() {
            val message = context.getString(R.string.google_sign_in_cancelled)
            onSignInError(message)
        }

        fun onEmailChange(text: String) {
            _state.update { it.copy(emailInput = text) }
        }

        fun onPasswordChange(text: String) {
            _state.update { it.copy(passwordInput = text) }
        }

        fun onEmailLoginClick() {
            val email = _state.value.emailInput
            val pass = _state.value.passwordInput

            if (email.isBlank() || pass.isBlank()) {
                onSignInError(context.getString(R.string.fill_all_fields))
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                val result = repository.signInWithEmail(email, pass)
                handleAuthResult(result)
            }
        }

        fun onRegisterClick() {
            val email = _state.value.emailInput
            val pass = _state.value.passwordInput

            if (email.isBlank() || pass.isBlank()) {
                onSignInError(context.getString(R.string.fill_all_fields))
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, errorMessage = null) }
                val result = repository.signUpWithEmail(email, pass)
                handleAuthResult(result)
            }
        }

        private fun handleAuthResult(result: Result<User>) {
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val error = result.exceptionOrNull()
                val errorMessage =
                    when (error) {
                        is FirebaseAuthInvalidUserException -> {
                            context.getString(
                                R.string.no_account_linked,
                            )
                        }

                        is FirebaseAuthUserCollisionException -> {
                            context.getString(
                                R.string.mail_already_registered,
                            )
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            context.getString(
                                R.string.incorrect_mail_password,
                            )
                        }

                        is FirebaseNetworkException -> {
                            context.getString(R.string.conection_error)
                        }

                        else -> {
                            context.getString(R.string.error_message, error?.message?.take(50))
                        }
                    }
                onSignInError(errorMessage)
            }
        }
    }
