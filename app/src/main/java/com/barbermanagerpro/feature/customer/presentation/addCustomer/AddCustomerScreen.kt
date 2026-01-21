package com.barbermanagerpro.feature.customer.presentation.addCustomer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.barbermanagerpro.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    viewModel: AddCustomerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current
    val customerSavedSuccessMessage = stringResource(R.string.customer_saved_success)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast
                .makeText(
                    context,
                    customerSavedSuccessMessage,
                    Toast.LENGTH_SHORT,
                ).show()

            onNavigateBack()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.new_client)) })
        },
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
            onSaveClick = viewModel::onSaveClick,
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
    onSaveClick: () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = state.firstName,
            onValueChange = onFirstNameChange,
            label = { Text(stringResource(R.string.customer_name)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.lastName,
            onValueChange = onLastNameChange,
            label = { Text(stringResource(R.string.customer_last_name)) },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = state.phone,
            onValueChange = onPhoneChange,
            label = { Text(stringResource(R.string.customer_phone)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            stringResource(R.string.customer_birthdate),
            style = MaterialTheme.typography.titleSmall,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.birthDay,
                onValueChange = onDayChange,
                label = { Text(stringResource(R.string.day)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = state.birthMonth,
                onValueChange = onMonthChange,
                label = { Text(stringResource(R.string.month)) },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = state.birthYear,
                onValueChange = onYearChange,
                label = { Text(stringResource(R.string.year)) },
                modifier = Modifier.weight(1.5f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(stringResource(R.string.save_customer))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddCustomerPreview() {
    AddCustomerContent(
        state =
            AddCustomerState(
                firstName = stringResource(R.string.placeholder_customer_name),
                phone =
                    stringResource(
                        R.string.placeholder_customer_phone,
                    ),
            ),
        onFirstNameChange = {},
        onLastNameChange = {},
        onPhoneChange = {},
        onDayChange = {},
        onMonthChange = {},
        onYearChange = {},
        onSaveClick = {},
    )
}
