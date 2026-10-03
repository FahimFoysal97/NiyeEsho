package com.snehonir.niyeesho.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = NiyeEshoPrimary,
    onPrimary = NiyeEshoOnPrimary,
    secondary = NiyeEshoSecondary,
    surface = NiyeEshoSurface,
    background = NiyeEshoBackground,
    onSurface = NiyeEshoOnSurface,
    tertiary = NiyeEshoTertiary,
    error = NiyeEshoError,
)

private val DarkColors = darkColorScheme(
    primary = ColorTokens.DarkPrimary,
    secondary = ColorTokens.DarkSecondary,
    tertiary = NiyeEshoTertiary,
    error = NiyeEshoError,
)

@Composable
fun NiyeEshoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = NiyeEshoTypography,
        shapes = NiyeEshoShapes,
        content = content,
    )
}

private object ColorTokens {
    val DarkPrimary = NiyeEshoPrimary.copy(alpha = 0.9f)
    val DarkSecondary = NiyeEshoSecondary.copy(alpha = 0.9f)
}
