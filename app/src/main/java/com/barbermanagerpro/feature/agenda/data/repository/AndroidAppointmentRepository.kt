package com.barbermanagerpro.feature.agenda.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import com.barbermanagerpro.R
import com.barbermanagerpro.feature.agenda.domain.model.AppointmentModel
import com.barbermanagerpro.feature.agenda.domain.model.CalendarModel
import com.barbermanagerpro.feature.agenda.domain.repository.AppointmentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.TimeZone
import javax.inject.Inject

class AndroidAppointmentRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : AppointmentRepository {
        override suspend fun getAvailableCalendars(): List<CalendarModel> =
            withContext(Dispatchers.IO) {
                val calendars = mutableListOf<CalendarModel>()

                val projection =
                    arrayOf(
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.OWNER_ACCOUNT,
                        CalendarContract.Calendars.CALENDAR_COLOR,
                        CalendarContract.Calendars.IS_PRIMARY,
                    )

                val contentResolver: ContentResolver = context.contentResolver
                val uri = CalendarContract.Calendars.CONTENT_URI

                try {
                    Timber.tag("AgendaRepo").d("Consultando ContentResolver para calendarios...")
                    val cursor =
                        contentResolver.query(
                            uri,
                            projection,
                            null,
                            null,
                            null,
                        )

                    if (cursor == null) {
                        Timber
                            .tag("AgendaRepo")
                            .e("El Cursor es NULL. Algo grave pasa con el ContentProvider.")
                    } else {
                        Timber
                            .tag("AgendaRepo")
                            .d("Cursor obtenido. Cantidad de filas encontradas: ${cursor.count}")
                    }

                    cursor?.use {
                        val idIdx = it.getColumnIndex(CalendarContract.Calendars._ID)
                        val nameIdx =
                            it.getColumnIndex(
                                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                            )
                        val accountIdx = it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
                        val ownerIdx = it.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT)
                        val colorIdx = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR)
                        val primaryIdx = it.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY)

                        while (it.moveToNext()) {
                            val id = it.getLong(idIdx)
                            val name = it.getString(nameIdx) ?: context.getString(R.string.no_name)
                            val account = it.getString(accountIdx) ?: ""
                            val owner = it.getString(ownerIdx) ?: ""
                            val color = it.getInt(colorIdx)
                            val isPrimary = it.getInt(primaryIdx) == 1
                            Timber
                                .tag("AgendaRepo")
                                .d("Calendario encontrado: ID=$id, Nombre=$name, Account=$account")
                            calendars.add(
                                CalendarModel(id, name, account, owner, color, isPrimary),
                            )
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Timber.tag("AgendaRepo").e("¡EXCEPCIÓN DE SEGURIDAD! No tenemos permisos.")
                    return@withContext emptyList()
                }
                Timber.tag("AgendaRepo").e("Error desconocido leyendo calendarios")
                return@withContext calendars
            }

        override suspend fun createAppointment(appointment: AppointmentModel): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    val values =
                        ContentValues().apply {
                            put(CalendarContract.Events.DTSTART, appointment.startTime)
                            put(CalendarContract.Events.DTEND, appointment.endTime)
                            put(CalendarContract.Events.TITLE, appointment.title)
                            put(CalendarContract.Events.DESCRIPTION, appointment.description)
                            put(CalendarContract.Events.CALENDAR_ID, appointment.calendarId)
                            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                        }

                    val uri =
                        context.contentResolver.insert(
                            CalendarContract.Events.CONTENT_URI,
                            values,
                        )

                    val eventId = uri?.lastPathSegment

                    if (eventId != null) {
                        Result.success(eventId)
                    } else {
                        Result.failure(Exception(context.getString(R.string.event_creation_error)))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }
