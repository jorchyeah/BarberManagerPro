package com.barbermanagerpro.feature.agenda.domain.model

data class AppointmentModel(
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val calendarId: Long,
)
