package com.snehonir.niyeesho.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.snehonir.niyeesho.core.designsystem.theme.NiyeEshoGreenPrimary

@Composable
fun AvatarImage(
    avatarUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    backgroundColor: Color = NiyeEshoGreenPrimary,
    textColor: Color = Color.White,
) {
    Surface(
        modifier = modifier.size(size),
        shape = CircleShape,
        color = backgroundColor,
    ) {
        Box(contentAlignment = Alignment.Center) {
            val initial = contentDescription?.trim()?.firstOrNull()?.uppercaseChar()?.toString()
            if (initial != null) {
                val textStyle = when {
                    size <= 24.dp -> MaterialTheme.typography.labelSmall
                    size <= 40.dp -> MaterialTheme.typography.titleMedium
                    else -> MaterialTheme.typography.titleLarge
                }
                Text(
                    text = initial,
                    style = textStyle,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = contentDescription,
                    tint = textColor,
                    modifier = Modifier.size(size * 0.6f),
                )
            }

            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}
