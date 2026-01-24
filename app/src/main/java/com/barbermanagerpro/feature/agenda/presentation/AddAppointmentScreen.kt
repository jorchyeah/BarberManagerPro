package com.barbermanagerpro.feature.agenda.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.barbermanagerpro.R
import com.barbermanagerpro.presentation.components.ClickableTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentScreen(
    viewModel: AppointmentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val permissionMessage = stringResource(R.string.calendar_permissions_required)

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedCalendarMenu by remember { mutableStateOf(false) }
    var isRequestingPermission by remember { mutableStateOf(true) }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.READ_CALENDAR] == true &&
                    permissions[Manifest.permission.WRITE_CALENDAR] == true
            if (granted) {
                viewModel.loadCalendars()
            } else {
                Toast
                    .makeText(
                        context,
                        permissionMessage,
                        Toast.LENGTH_LONG,
                    ).show()
            }
            isRequestingPermission = false
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
            ),
        )
    }

    LaunchedEffect(state.successMessage, state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }

        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
            onNavigateBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text(stringResource(R.string.new_appointment)) })
            },
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val selectedCalendar = state.calendars.find { it.id == state.selectedCalendarId }
                val calendarLabel =
                    selectedCalendar?.name ?: stringResource(R.string.select_calendar)

                ExposedDropdownMenuBox(
                    expanded = expandedCalendarMenu,
                    onExpandedChange = { expandedCalendarMenu = !expandedCalendarMenu },
                ) {
                    OutlinedTextField(
                        value = calendarLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.save_in_calendar)) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                stringResource(R.string.unfold),
                            )
                        },
                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            ),
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCalendarMenu,
                        onDismissRequest = { expandedCalendarMenu = false },
                    ) {
                        if (state.calendars.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.no_calendars_found)) },
                                onClick = { expandedCalendarMenu = false },
                            )
                        } else {
                            state.calendars.forEach { calendar ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                calendar.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                            Text(
                                                calendar.accountName,
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.onCalendarSelected(calendar.id)
                                        expandedCalendarMenu = false
                                    },
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = state.customerName,
                    onValueChange = viewModel::onCustomerNameChange,
                    label = { Text(stringResource(R.string.customer_name)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                val dateFormatter = SimpleDateFormat("EEEE dd MMMM", Locale.getDefault())
                val dateString = dateFormatter.format(Date(state.selectedDate))

                ClickableTextField(
                    value = dateString,
                    label = stringResource(R.string.date),
                    trailingIcon = Icons.Default.DateRange,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                val formattedTime =
                    remember(state.selectedTimeHour, state.selectedTimeMinute) {
                        val cal =
                            java.util.Calendar.getInstance().apply {
                                set(java.util.Calendar.HOUR_OF_DAY, state.selectedTimeHour)
                                set(java.util.Calendar.MINUTE, state.selectedTimeMinute)
                            }
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
                    }

                ClickableTextField(
                    value = formattedTime,
                    label = stringResource(R.string.time),
                    trailingIcon = Icons.Default.AccessTime,
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = state.customerNote,
                    onValueChange = viewModel::onNoteChange,
                    label = { Text(stringResource(R.string.notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = viewModel::saveAppointment,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text(stringResource(R.string.add_appointment))
                    }
                }
            }
        }
    }

    if (isRequestingPermission) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Color.Black
                            .copy(alpha = 0.6f),
                    ).zIndex(1f)
                    .clickable(enabled = true, onClick = {}),
        )
    }

    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = state.selectedDate)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDateSelected(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    },
                ) { Text(stringResource(R.string.accept)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                ) { Text(stringResource(R.string.cancel)) }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState =
            rememberTimePickerState(
                initialHour = state.selectedTimeHour,
                initialMinute = state.selectedTimeMinute,
                is24Hour = false,
            )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onTimeSelected(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    },
                ) { Text(stringResource(R.string.accept)) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false },
                ) { Text(stringResource(R.string.cancel)) }
            },
            text = {
                TimePicker(state = timePickerState)
            },
        )
    }
}
