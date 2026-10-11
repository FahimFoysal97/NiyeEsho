package com.snehonir.niyeesho.features.shopping_list.presentation.list.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.snehonir.niyeesho.core.designsystem.component.AppTextField
import com.snehonir.niyeesho.core.designsystem.theme.AppStrings
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun CreateListDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = NiyeEshoDarkSurface,
        title = {
            Text(
                text = "Create a shopping list",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        },
        text = {
            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = UiText.Plain(AppStrings.HINT_LIST_NAME),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (title.isNotBlank()) onConfirm(title.trim()) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenPrimary),
            ) { Text(AppStrings.ACTION_CREATE) }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenDarkMuted),
            ) { Text(AppStrings.ACTION_CANCEL) }
        },
    )
}
