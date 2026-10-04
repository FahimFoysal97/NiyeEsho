package com.snehonir.niyeesho.features.shopping_list.presentation.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoDarkSurface
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenDarkMuted
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun AddItemDialog(
    onConfirm: (String, String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = NiyeEshoDarkSurface,
        title = {
            Text(
                text = "Add new item",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                AppTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = UiText.Plain("Item name (e.g. Milk)"),
                )
                AppTextField(
                    value = itemQty,
                    onValueChange = { itemQty = it },
                    label = UiText.Plain("Quantity (optional, e.g. 2 ltr)"),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (itemName.isNotBlank()) onConfirm(itemName.trim(), itemQty.ifBlank { null }) },
                enabled = itemName.isNotBlank(),
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenPrimary),
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = NiyeEshoGreenDarkMuted),
            ) { Text("Cancel") }
        },
    )
}
