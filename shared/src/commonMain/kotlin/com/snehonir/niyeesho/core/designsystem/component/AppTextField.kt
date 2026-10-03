package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: UiText,
    modifier: Modifier = Modifier,
    placeholder: UiText? = null,
    isError: Boolean = false,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label.resolve()) },
        placeholder = placeholder?.let { { Text(it.resolve()) } },
        isError = isError,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        ),
    )
}