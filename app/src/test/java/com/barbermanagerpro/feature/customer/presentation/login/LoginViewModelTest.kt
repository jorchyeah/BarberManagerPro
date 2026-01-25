package com.barbermanagerpro.feature.customer.presentation.login

import android.content.Context
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.auth.domain.repository.AuthRepository
import com.barbermanagerpro.feature.customer.domain.model.User
import com.barbermanagerpro.util.MainDispatcherRule
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: AuthRepository
    private lateinit var context: Context
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        repository = mockk()
        context = mockk()

        // Mockeamos los strings básicos que usa el ViewModel
        every { context.getString(R.string.null_token_error) } returns "Token nulo"
        every { context.getString(R.string.unknown_error) } returns "Error desconocido"
        every { context.getString(R.string.fill_all_fields) } returns "Llena los campos"
        every { context.getString(R.string.no_account_linked) } returns "No existe cuenta"

        viewModel = LoginViewModel(repository, context)
    }

    @Test
    fun `onSignInSuccess with valid token updates state to success`() {
        // Given
        val token = "valid_token"
        val user = User("1", "Test", "test@test.com")
        coEvery { repository.signInWithGoogle(token) } returns Result.success(user)

        // When
        viewModel.onSignInSuccess(token)

        // Then
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onSignInSuccess with null token sets error message`() {
        // When
        viewModel.onSignInSuccess(null)

        // Then
        assertEquals("Token nulo", viewModel.state.value.errorMessage)
    }

    @Test
    fun `onEmailLoginClick with empty fields sets error`() {
        // Given
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")

        // When
        viewModel.onEmailLoginClick()

        // Then
        assertEquals("Llena los campos", viewModel.state.value.errorMessage)
    }

    @Test
    fun `onEmailLoginClick success updates state`() {
        // Given
        val email = "test@test.com"
        val pass = "123456"
        val user = User("1", "Test", email)

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(pass)

        coEvery { repository.signInWithEmail(email, pass) } returns Result.success(user)

        // When
        viewModel.onEmailLoginClick()

        // Then
        assertTrue(viewModel.state.value.isSuccess)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `handleAuthResult maps Firebase exception to correct string`() {
        // Given
        val email = "noexist@test.com"
        val pass = "123"
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(pass)

        val exception = mockk<FirebaseAuthInvalidUserException>()
        coEvery { repository.signInWithEmail(email, pass) } returns Result.failure(exception)

        // When
        viewModel.onEmailLoginClick()

        // Then
        assertEquals("No existe cuenta", viewModel.state.value.errorMessage)
    }
}
