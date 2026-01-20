package com.barbermanagerpro.feature.customer.domain.repository

import com.barbermanagerpro.feature.customer.domain.model.Customer

interface CustomerRepository {
    suspend fun saveCustomer(customer: Customer): Result<Unit>
}
