package com.barbermanagerpro.feature.agenda.domain.model

data class CalendarModel(
    val id: Long,
    val name: String,
    val accountName: String,
    val ownerName: String,
    val color: Int,
    val isPrimary: Boolean,
)
