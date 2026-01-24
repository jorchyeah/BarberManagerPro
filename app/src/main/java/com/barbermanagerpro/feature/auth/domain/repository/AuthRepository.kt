package com.barbermanagerpro.feature.auth.domain.repository

import com.barbermanagerpro.feature.customer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun signInWithGoogle(idToken: String): Result<User>

    suspend fun signOut()

    suspend fun signUpWithEmail(
        email: String,
        pass: String,
    ): Result<User>

    suspend fun signInWithEmail(
        email: String,
        pass: String,
    ): Result<User>
}
