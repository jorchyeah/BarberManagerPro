package com.barbermanagerpro.feature.customer.domain.repository

import com.barbermanagerpro.feature.customer.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    suspend fun saveCustomer(customer: Customer): Result<Unit>

    fun getCustomers(): Flow<List<Customer>>

    suspend fun getCustomerById(id: String): Customer?

    suspend fun deleteCustomer(id: String): Result<Unit>
}
