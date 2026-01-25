package com.barbermanagerpro.feature.customer.data.repository

import android.content.Context
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreCustomerRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val auth: FirebaseAuth,
        @ApplicationContext private val context: Context,
    ) : CustomerRepository {
        private fun getUserCustomersCollection(): CollectionReference {
            val userId =
                auth.currentUser?.uid
                    ?: throw IllegalStateException(context.getString(R.string.unauthenticated_user))
            return firestore.collection("users").document(userId).collection("customers")
        }

        override suspend fun saveCustomer(customer: Customer): Result<Unit> =
            try {
                val collection = getUserCustomersCollection()

                val document =
                    if (customer.id.isBlank()) {
                        collection.document()
                    } else {
                        collection.document(customer.id)
                    }

                val customerToSave =
                    customer.copy(
                        id = document.id,
                        shopId = auth.currentUser!!.uid,
                    )
                document.set(customerToSave).await()

                Result.success(Unit)
            } catch (e: TimeoutCancellationException) {
                e.printStackTrace()
                Result.failure(Exception(context.getString(R.string.save_customer_timeout)))
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }

        override fun getCustomers(): Flow<List<Customer>> =
            callbackFlow {
                val collection =
                    try {
                        getUserCustomersCollection()
                    } catch (e: Exception) {
                        close(e)
                        return@callbackFlow
                    }

                val subscription =
                    collection.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val customers = snapshot.toObjects(Customer::class.java)
                            trySend(customers)
                        }
                    }
                awaitClose { subscription.remove() }
            }

        override suspend fun getCustomerById(id: String): Customer? =
            try {
                val snapshot = getUserCustomersCollection().document(id).get().await()
                snapshot.toObject(Customer::class.java)
            } catch (e: Exception) {
                null
            }

        override suspend fun deleteCustomer(id: String): Result<Unit> =
            try {
                getUserCustomersCollection().document(id).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
