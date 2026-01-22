package com.barbermanagerpro.feature.auth.data.repository

import com.barbermanagerpro.feature.customer.domain.model.User
import com.barbermanagerpro.feature.customer.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    var shouldFailLogin = false

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        if (shouldFailLogin) {
            return Result.failure(Exception("Error simulado de Google Sign-In"))
        }

        val fakeUser =
            User(
                id = "test_user_123",
                displayName = "Barbero Test",
                email = "test@barberia.com",
            )

        _currentUser.value = fakeUser
        return Result.success(fakeUser)
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    fun setLoggedInUser(user: User) {
        _currentUser.value = user
    }
}
