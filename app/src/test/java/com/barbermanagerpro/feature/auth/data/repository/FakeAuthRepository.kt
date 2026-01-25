package com.barbermanagerpro.feature.auth.data.repository

import com.barbermanagerpro.feature.auth.domain.repository.AuthRepository
import com.barbermanagerpro.feature.customer.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

class FakeAuthRepository
    @Inject
    constructor() : AuthRepository {
        private val registeredUsers = mutableListOf<User>()
        private val _currentUser = MutableStateFlow<User?>(null)

        override val currentUser: Flow<User?> = _currentUser.asStateFlow()

        override suspend fun signInWithGoogle(idToken: String): Result<User> {
            delay(1000)
            val user =
                User(
                    id = "google_user_id",
                    displayName = "Google User",
                    email = "google@test.com",
                )
            _currentUser.value = user
            return Result.success(user)
        }

        override suspend fun signUpWithEmail(
            email: String,
            pass: String,
        ): Result<User> {
            delay(1000)

            if (registeredUsers.any { it.email == email }) {
                return Result.failure(Exception("El correo ya está registrado (Fake)"))
            }

            if (pass == "error") {
                return Result.failure(Exception("Error simulado de red"))
            }

            val newUser =
                User(
                    id = UUID.randomUUID().toString(),
                    displayName = "Nuevo Usuario",
                    email = email,
                )

            registeredUsers.add(newUser)
            _currentUser.value = newUser

            return Result.success(newUser)
        }

        override suspend fun signInWithEmail(
            email: String,
            pass: String,
        ): Result<User> {
            delay(1000)

            if (pass == "error") {
                return Result.failure(Exception("Contraseña incorrecta (Simulada)"))
            }

            val user = registeredUsers.find { it.email == email }

            return if (user != null) {
                _currentUser.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado. Regístrate primero."))
            }
        }

        override suspend fun signOut() {
            delay(500)
            _currentUser.value = null
        }
    }
