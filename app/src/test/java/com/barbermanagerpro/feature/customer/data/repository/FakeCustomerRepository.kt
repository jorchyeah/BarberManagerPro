package com.barbermanagerpro.feature.customer.data.repository

import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCustomerRepository : CustomerRepository {
    private val inMemoryDb = MutableStateFlow<List<Customer>>(emptyList())

    override suspend fun saveCustomer(customer: Customer): Result<Unit> {
        val currentList = inMemoryDb.value.toMutableList()

        val index = currentList.indexOfFirst { it.id == customer.id }
        if (index != -1) {
            currentList[index] = customer
        } else {
            val customerWithId =
                if (customer.id.isBlank()) {
                    customer.copy(id = "fake_id_${currentList.size + 1}")
                } else {
                    customer
                }
            currentList.add(customerWithId)
        }

        inMemoryDb.value = currentList
        return Result.success(Unit)
    }

    fun getCustomers(): Flow<List<Customer>> = inMemoryDb

    fun clearDb() {
        inMemoryDb.value = emptyList()
    }
}
