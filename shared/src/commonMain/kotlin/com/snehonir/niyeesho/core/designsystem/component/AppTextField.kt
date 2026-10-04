package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkBg
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenLight
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
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
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = NiyeEshoDarkBg,
            focusedContainerColor = NiyeEshoDarkBg,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedLabelColor = NiyeEshoGreenLight,
            focusedLabelColor = NiyeEshoGreenPrimary,
            unfocusedPlaceholderColor = NiyeEshoGreenDarkMuted,
            focusedPlaceholderColor = NiyeEshoGreenLight,
            unfocusedBorderColor = NiyeEshoGreenDarkMuted,
            focusedBorderColor = NiyeEshoGreenPrimary,
        ),
    )
}
