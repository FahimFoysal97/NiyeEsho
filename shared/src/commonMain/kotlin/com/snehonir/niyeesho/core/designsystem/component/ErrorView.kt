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
fun ErrorView(
    message: UiText,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Text(message.resolve())
        if (action != null) {
            Spacer(Modifier.height(Spacing.sm))
            action()
        }
    }
}
