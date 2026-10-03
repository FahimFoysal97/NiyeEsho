package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.snehonir.niyeesho.core.designsystem.theme.Spacing
import com.snehonir.niyeesho.core.utils.UiText

@Composable
fun EmptyStateView(
    title: UiText,
    modifier: Modifier = Modifier,
    description: UiText? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Text(title.resolve())
        description?.let {
            Spacer(Modifier.height(Spacing.sm))
            Text(it.resolve())
        }
        if (action != null) {
            Spacer(Modifier.height(Spacing.md))
            action()
        }
    }
}
