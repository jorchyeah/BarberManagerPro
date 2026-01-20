package com.barbermanagerpro.feature.customer.data.repository

import com.barbermanagerpro.core.COLLECTION_CUSTOMERS
import com.barbermanagerpro.core.CURRENT_SHOP_ID
import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreCustomerRepository
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : CustomerRepository {
        override suspend fun saveCustomer(customer: Customer): Result<Unit> =
            try {
                val customerToSave = customer.copy(shopId = CURRENT_SHOP_ID)

                val documentRef =
                    if (
                        customerToSave.id.isBlank()
                    ) {
                        firestore
                            .collection(
                                COLLECTION_CUSTOMERS,
                            ).document()
                    } else {
                        firestore
                            .collection(
                                COLLECTION_CUSTOMERS,
                            ).document(customerToSave.id)
                    }

                val finalCustomer = customerToSave.copy(id = documentRef.id)

                documentRef.set(finalCustomer).await()

                Result.success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
    }
