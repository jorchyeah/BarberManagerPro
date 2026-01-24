package com.barbermanagerpro.feature.agenda.presentation

import android.content.Context
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.agenda.domain.model.AppointmentModel
import com.barbermanagerpro.feature.agenda.domain.model.CalendarModel
import com.barbermanagerpro.feature.agenda.domain.repository.AppointmentRepository
import com.barbermanagerpro.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: AppointmentRepository
    private lateinit var context: Context
    private lateinit var viewModel: AppointmentViewModel

    private val primaryCalendar = CalendarModel(1, "Primary", "acc1", "owner", 0, true)
    private val secondaryCalendar =
        CalendarModel(2, "Secondary", "test@gmail.com", "owner", 0, false)
    private val otherCalendar = CalendarModel(3, "Other", "local", "owner", 0, false)

    @Before
    fun setUp() {
        repository = mockk()
        context = mockk()

        every { context.getString(R.string.appointment_creation_success) } returns "Cita creada"
        every { context.getString(R.string.no_calendar_selected) } returns "Sin calendario"
        every { context.getString(R.string.customer_name_required) } returns "Nombre requerido"

        coEvery { repository.getAvailableCalendars() } returns emptyList()
    }

    @Test
    fun `loadCalendars prioritizes GMAIL account over system primary`() {
        // Given
        val systemPrimary = CalendarModel(1, "Phone", "local_account", "owner", 0, true)
        val gmailCal = CalendarModel(2, "Gmail", "juan@gmail.com", "owner", 0, false)

        coEvery { repository.getAvailableCalendars() } returns listOf(systemPrimary, gmailCal)

        // When
        viewModel = AppointmentViewModel(repository, context)

        // Then
        assertEquals(gmailCal.id, viewModel.state.value.selectedCalendarId)
    }

    @Test
    fun `loadCalendars prioritizes EMAIL account over system primary`() {
        // Given
        val systemPrimary = CalendarModel(1, "Phone", "local_account", "owner", 0, true)
        val outlookCal = CalendarModel(3, "Outlook", "juan@outlook.com", "owner", 0, false)

        coEvery { repository.getAvailableCalendars() } returns listOf(systemPrimary, outlookCal)

        // When
        viewModel = AppointmentViewModel(repository, context)

        // Then
        assertEquals(outlookCal.id, viewModel.state.value.selectedCalendarId)
    }

    @Test
    fun `loadCalendars falls back to system primary if no emails found`() {
        // Given
        val localCal = CalendarModel(4, "Local", "local_acc", "owner", 0, false)
        val systemPrimary = CalendarModel(1, "Phone", "primary_acc", "owner", 0, true)

        coEvery { repository.getAvailableCalendars() } returns listOf(localCal, systemPrimary)

        // When
        viewModel = AppointmentViewModel(repository, context)

        // Then
        assertEquals(systemPrimary.id, viewModel.state.value.selectedCalendarId)
    }

    @Test
    fun `loadCalendars selects email calendar if no primary exists`() {
        // Given
        coEvery { repository.getAvailableCalendars() } returns
            listOf(otherCalendar, secondaryCalendar)

        // When
        viewModel = AppointmentViewModel(repository, context)

        // Then
        assertEquals(secondaryCalendar.id, viewModel.state.value.selectedCalendarId)
    }

    @Test
    fun `saveAppointment with empty name sets error`() {
        // Given
        viewModel = AppointmentViewModel(repository, context)
        viewModel.onCalendarSelected(1L)
        viewModel.onCustomerNameChange("")

        // When
        viewModel.saveAppointment()

        // Then
        assertEquals("Nombre requerido", viewModel.state.value.error)
    }

    @Test
    fun `saveAppointment calls repository with correct data`() {
        // Given
        coEvery { repository.getAvailableCalendars() } returns listOf(primaryCalendar)
        coEvery { repository.createAppointment(any()) } returns Result.success("100")

        viewModel = AppointmentViewModel(repository, context)

        viewModel.onCustomerNameChange("Juan Perez")
        viewModel.onNoteChange("Corte bajo")
        viewModel.onTimeSelected(14, 30)

        // When
        viewModel.saveAppointment()

        // Then
        val slot = slot<AppointmentModel>()
        coVerify { repository.createAppointment(capture(slot)) }

        assertEquals("Juan Perez", slot.captured.title)
        assertEquals("Corte bajo", slot.captured.description)
        assertEquals(primaryCalendar.id, slot.captured.calendarId)

        assertEquals("Cita creada", viewModel.state.value.successMessage)
        assertEquals(100L, viewModel.state.value.createdAppointmentId)
    }
}
