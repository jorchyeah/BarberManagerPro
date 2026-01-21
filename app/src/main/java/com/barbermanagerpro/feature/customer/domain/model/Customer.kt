package com.barbermanagerpro.feature.customer.domain.model

import java.util.Date

data class Customer(
    val id: String = "",
    val shopId: String = "",
    val legacyId: String? = null,
    val personalInfo: PersonalInfo = PersonalInfo(),
    val metrics: CustomerMetrics = CustomerMetrics(),
    val audit: AuditInfo = AuditInfo(),
)

data class PersonalInfo(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String? = null,
    val birthDate: BirthDate? = null,
)

data class BirthDate(
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
) {
    fun toDisplayString(): String = "$day/$month/$year"
}

data class CustomerMetrics(
    val totalVisits: Int = 0,
    val visitsSinceLastReward: Int = 0,
    val lastVisit: Date? = null,
)

data class AuditInfo(
    val createdAt: Date = Date(),
    val isActive: Boolean = true,
)
