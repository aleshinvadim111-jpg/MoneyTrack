package com.example.moneytrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ReceiptColors = lightColorScheme(
    primary = Ink,
    onPrimary = White,
    secondary = Lime,
    onSecondary = Ink,
    background = Paper,
    onBackground = Ink,
    surface = White,
    onSurface = Ink,
    surfaceVariant = Paper,
    onSurfaceVariant = Muted,
    outline = Dotted,
    error = Danger,
    onError = White,
)

// Концепт «Чек» — всегда светлая «бумажная» тема
@Composable
fun MoneyTrackTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ReceiptColors,
        typography = Typography,
        content = content,
    )
}