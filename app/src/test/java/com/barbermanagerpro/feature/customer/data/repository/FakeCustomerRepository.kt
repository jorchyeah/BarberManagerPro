package com.barbermanagerpro.feature.customer.data.repository

import com.barbermanagerpro.feature.customer.domain.model.Customer
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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

    override fun getCustomers(): Flow<List<Customer>> = inMemoryDb

    override suspend fun getCustomerById(id: String): Customer? =
        inMemoryDb.value.find { it.id == id }

    override suspend fun deleteCustomer(id: String): Result<Unit> {
        inMemoryDb.update { currentList ->
            currentList.filter { it.id != id }
        }
        return Result.success(Unit)
    }

    fun clearDb() {
        inMemoryDb.value = emptyList()
    }
}
