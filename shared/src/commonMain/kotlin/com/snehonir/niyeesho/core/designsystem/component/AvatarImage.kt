package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage

@Composable
fun AvatarImage(
    avatarUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = AvatarDefaults.Size,
) {
    if (avatarUrl.isNullOrBlank()) {
        Text(
            text = "?",
            modifier = modifier.size(size).clip(CircleShape),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
        )
    } else {
        AsyncImage(
            model = avatarUrl,
            contentDescription = contentDescription,
            modifier = modifier.size(size).clip(CircleShape),
        )
    }
}

private object AvatarDefaults {
    val Size = com.snehonir.niyeesho.core.designsystem.theme.Spacing.xl +
        com.snehonir.niyeesho.core.designsystem.theme.Spacing.sm
}
