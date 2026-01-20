package com.barbermanagerpro.di

import com.barbermanagerpro.feature.customer.data.repository.FirestoreCustomerRepository
import com.barbermanagerpro.feature.customer.domain.repository.CustomerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCustomerRepository(impl: FirestoreCustomerRepository): CustomerRepository
}
