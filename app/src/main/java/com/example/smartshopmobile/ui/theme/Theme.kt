package com.example.smartshopmobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// SmartShop is a Dark-first design system.
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = Muted,
    error = Error,
    onError = OnError,
    outline = Outline
)

@Composable
fun SmartShopMobileTheme(
    // We default to dark theme to enforce the brand identity, but respect system override if needed.
    // However, the design guide implies a strict dark mode look.
    darkTheme: Boolean = true, 
    content: @Composable () -> Unit
) {
    // We ignore dynamic color to maintain strict brand identity
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}