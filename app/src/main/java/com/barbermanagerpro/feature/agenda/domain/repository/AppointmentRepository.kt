package com.barbermanagerpro.feature.agenda.domain.repository

import com.barbermanagerpro.feature.agenda.domain.model.AppointmentModel
import com.barbermanagerpro.feature.agenda.domain.model.CalendarModel

interface AppointmentRepository {
    suspend fun getAvailableCalendars(): List<CalendarModel>

    suspend fun createAppointment(appointment: AppointmentModel): Result<String>
}
