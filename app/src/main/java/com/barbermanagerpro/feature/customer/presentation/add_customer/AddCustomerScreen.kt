package com.barbermanagerpro.feature.customer.presentation.add_customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    viewModel: AddCustomerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Nuevo Cliente") })
        }
    ) { paddingValues ->
        AddCustomerContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onFirstNameChange = viewModel::onFirstNameChange,
            onLastNameChange = viewModel::onLastNameChange,
            onPhoneChange = viewModel::onPhoneChange,
            onDayChange = viewModel::onBirthDayChange,
            onMonthChange = viewModel::onBirthMonthChange,
            onYearChange = viewModel::onBirthYearChange,
            onSaveClick = viewModel::onSaveClick
        )
    }
}

@Composable
fun AddCustomerContent(
    modifier: Modifier = Modifier,
    state: AddCustomerState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.phone,
            onValueChange = onPhoneChange,
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Fecha de Nacimiento", style = MaterialTheme.typography.titleSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.birthDay,
                onValueChange = onDayChange,
                label = { Text("Día") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = state.birthMonth,
                onValueChange = onMonthChange,
                label = { Text("Mes") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = state.birthYear,
                onValueChange = onYearChange,
                label = { Text("Año") },
                modifier = Modifier.weight(1.5f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Guardar Cliente")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCustomerPreview() {
    AddCustomerContent(
        state = AddCustomerState(firstName = "Jorge", phone = "555"),
        onFirstNameChange = {},
        onLastNameChange = {},
        onPhoneChange = {},
        onDayChange = {},
        onMonthChange = {},
        onYearChange = {},
        onSaveClick = {}
    )
}