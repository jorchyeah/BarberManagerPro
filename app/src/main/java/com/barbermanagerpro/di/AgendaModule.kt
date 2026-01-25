package com.barbermanagerpro.di

import com.barbermanagerpro.feature.agenda.data.repository.AndroidAppointmentRepository
import com.barbermanagerpro.feature.agenda.domain.repository.AppointmentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AgendaModule {
    @Binds
    abstract fun bindAgendaRepository(impl: AndroidAppointmentRepository): AppointmentRepository
}
