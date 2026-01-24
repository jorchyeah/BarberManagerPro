package com.barbermanagerpro.feature.auth.data.repository

import android.content.Context
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.auth.domain.repository.AuthRepository
import com.barbermanagerpro.feature.customer.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthRepository
    @Inject
    constructor(
        private val auth: FirebaseAuth,
        @ApplicationContext private val context: Context,
    ) : AuthRepository {
        override val currentUser: Flow<User?> =
            callbackFlow {
                val listener =
                    FirebaseAuth.AuthStateListener { firebaseAuth ->
                        val user = firebaseAuth.currentUser?.toDomain()
                        trySend(user)
                    }
                auth.addAuthStateListener(listener)
                awaitClose { auth.removeAuthStateListener(listener) }
            }

        override suspend fun signInWithGoogle(idToken: String): Result<User> =
            try {
                Timber
                    .tag("AuthRepo")
                    .d("Intentando intercambiar token de Google por credencial Firebase")
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user?.toDomain()
                if (user == null) {
                    Timber
                        .tag("AuthRepo")
                        .e("signInWithCredential exitoso, pero el usuario es nulo")
                    throw Exception(context.getString(R.string.null_user))
                }

                Timber.tag("AuthRepo").d("Usuario autenticado correctamente: ${user.email}")
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun signOut() {
            Timber.tag("AuthRepo").e("Excepción crítica en signInWithGoogle")
            auth.signOut()
        }

        override suspend fun signUpWithEmail(
            email: String,
            pass: String,
        ): Result<User> =
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
                val user =
                    authResult.user?.toDomain()
                        ?: throw Exception(context.getString(R.string.error_creating_user))
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun signInWithEmail(
            email: String,
            pass: String,
        ): Result<User> =
            try {
                val authResult = auth.signInWithEmailAndPassword(email, pass).await()
                val user =
                    authResult.user?.toDomain()
                        ?: throw Exception(context.getString(R.string.error_login_in_user))
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }

        private fun com.google.firebase.auth.FirebaseUser.toDomain(): User =
            User(
                id = this.uid,
                displayName = this.displayName,
                email = this.email,
            )
    }
