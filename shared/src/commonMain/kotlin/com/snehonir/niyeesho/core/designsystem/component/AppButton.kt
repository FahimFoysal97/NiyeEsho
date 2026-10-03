package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun AppButton(
    text: UiText,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    minHeight: Dp = Spacing.xl,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = Spacing.lg,
            vertical = Spacing.sm,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                strokeWidth = Spacing.xs / 2,
            )
        } else {
            Text(text.resolve())
        }
    }
}
