package com.barbermanagerpro.feature.auth.data.repository

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class FakeAuthRepositoryTest {
    private lateinit var fakeRepository: FakeAuthRepository

    @Before
    fun setUp() {
        fakeRepository = FakeAuthRepository()
    }

    @Test
    fun `signUpWithEmail adds user and emits it`() =
        runBlocking {
            // Given
            val email = "new@test.com"
            val pass = "123456"

            // When
            val result = fakeRepository.signUpWithEmail(email, pass)
            val currentUser = fakeRepository.currentUser.first()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(email, result.getOrNull()?.email)
            assertEquals(email, currentUser?.email)
        }

    @Test
    fun `signInWithEmail works for existing user`() =
        runBlocking {
            // Given
            val email = "existing@test.com"
            fakeRepository.signUpWithEmail(email, "pass")
            fakeRepository.signOut() // Salimos

            // When
            val result = fakeRepository.signInWithEmail(email, "pass")

            // Then
            assertTrue(result.isSuccess)
            assertEquals(email, fakeRepository.currentUser.first()?.email)
        }

    @Test
    fun `signInWithEmail fails for non-existing user`() =
        runBlocking {
            // When
            val result = fakeRepository.signInWithEmail("ghost@test.com", "pass")

            // Then
            assertTrue(result.isFailure)
        }

    @Test
    fun `signInWithEmail fails with specific password error`() =
        runBlocking {
            // When
            val result = fakeRepository.signInWithEmail("any@test.com", "error")

            // Then
            assertTrue(result.isFailure)
            assertEquals("Contraseña incorrecta (Simulada)", result.exceptionOrNull()?.message)
        }
}
