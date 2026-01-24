package com.barbermanagerpro.feature.agenda.presentation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.agenda.domain.model.AppointmentModel
import com.barbermanagerpro.feature.agenda.domain.model.CalendarModel
import com.barbermanagerpro.feature.agenda.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel
    @Inject
    constructor(
        private val repository: AppointmentRepository,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        data class UiState(
            val calendars: List<CalendarModel> = emptyList(),
            val selectedCalendarId: Long? = null,
            val isLoading: Boolean = false,
            val error: String? = null,
            val successMessage: String? = null,
            val selectedDate: Long = System.currentTimeMillis(),
            val selectedTimeHour: Int = 12,
            val selectedTimeMinute: Int = 0,
            val customerName: String = "",
            val customerNote: String = "",
            val createdAppointmentId: Long? = null,
        )

        private val _state = MutableStateFlow(UiState())
        val state: StateFlow<UiState> = _state.asStateFlow()

        init {
            loadCalendars()
        }

        fun loadCalendars() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                val calendars = repository.getAvailableCalendars()

                val defaultCalendar =
                    calendars.find {
                        it.accountName.contains("@") &&
                            it.accountName.contains("gmail", ignoreCase = true)
                    }
                        ?: calendars.find { it.accountName.contains("@") }
                        ?: calendars.find { it.isPrimary }
                        ?: calendars.firstOrNull()

                _state.update {
                    it.copy(
                        isLoading = false,
                        calendars = calendars,
                        selectedCalendarId = defaultCalendar?.id,
                    )
                }
            }
        }

        fun onCalendarSelected(calendarId: Long) {
            _state.update { it.copy(selectedCalendarId = calendarId) }
        }

        fun onDateSelected(millis: Long?) {
            millis?.let { _state.update { s -> s.copy(selectedDate = it) } }
        }

        fun onTimeSelected(
            hour: Int,
            minute: Int,
        ) {
            _state.update { it.copy(selectedTimeHour = hour, selectedTimeMinute = minute) }
        }

        fun onCustomerNameChange(name: String) {
            _state.update { it.copy(customerName = name) }
        }

        fun onNoteChange(note: String) {
            _state.update { it.copy(customerNote = note) }
        }

        fun clearMessages() {
            _state.update { it.copy(error = null, successMessage = null) }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun saveAppointment() {
            val currentState = _state.value
            if (currentState.selectedCalendarId == null) {
                _state.update { it.copy(error = context.getString(R.string.no_calendar_selected)) }
                return
            }

            if (currentState.customerName.isBlank()) {
                _state.update {
                    it.copy(
                        error = context.getString(R.string.customer_name_required),
                    )
                }
                return
            }

            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }

                val localDate =
                    Instant
                        .ofEpochMilli(currentState.selectedDate)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                val startDateTime =
                    LocalDateTime.of(
                        localDate,
                        java.time.LocalTime.of(
                            currentState.selectedTimeHour,
                            currentState.selectedTimeMinute,
                        ),
                    )
                val endDateTime = startDateTime.plusHours(1)

                val startMillis =
                    startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endMillis =
                    endDateTime
                        .atZone(
                            ZoneId.systemDefault(),
                        ).toInstant()
                        .toEpochMilli()

                val appointment =
                    AppointmentModel(
                        title = currentState.customerName,
                        description = currentState.customerNote,
                        startTime = startMillis,
                        endTime = endMillis,
                        calendarId = currentState.selectedCalendarId,
                    )

                val result = repository.createAppointment(appointment)

                if (result.isSuccess) {
                    val eventIdStr = result.getOrNull()
                    val eventId = eventIdStr?.toLongOrNull()

                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage =
                                context.getString(
                                    R.string.appointment_creation_success,
                                ),
                            createdAppointmentId = eventId,
                            customerName = "",
                            customerNote = "",
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = context.getString(R.string.error_message),
                        )
                    }
                }
            }
        }
    }
