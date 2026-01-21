package com.barbermanagerpro.feature.customer.data.repository

import android.content.Context
import com.barbermanagerpro.R
import com.barbermanagerpro.core.COLLECTION_CUSTOMERS
import com.barbermanagerpro.core.CURRENT_SHOP_ID
import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class FirestoreCustomerRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        @ApplicationContext private val context: Context,
    ) : CustomerRepository {
        override suspend fun saveCustomer(customer: Customer): Result<Unit> =
            try {
                withTimeout(5000L) {
                    val customerToSave = customer.copy(shopId = CURRENT_SHOP_ID)

                    val documentRef =
                        if (customerToSave.id.isBlank()) {
                            firestore.collection(COLLECTION_CUSTOMERS).document()
                        } else {
                            firestore.collection(COLLECTION_CUSTOMERS).document(customerToSave.id)
                        }

                    val finalCustomer = customerToSave.copy(id = documentRef.id)

                    documentRef.set(finalCustomer).await()
                }

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
                val query =
                    firestore
                        .collection(COLLECTION_CUSTOMERS)
                        .whereEqualTo("shopId", CURRENT_SHOP_ID)
                // .orderBy("personalInfo.firstName")

                val registration =
                    query.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val customers =
                                snapshot.documents.mapNotNull { doc ->
                                    doc.toObject(Customer::class.java)?.copy(id = doc.id)
                                }
                            trySend(customers)
                        }
                    }

                awaitClose { registration.remove() }
            }
    }
