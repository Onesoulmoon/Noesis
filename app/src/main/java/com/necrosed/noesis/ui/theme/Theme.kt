package com.necrosed.noesis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoesisColorScheme = lightColorScheme(
    primary             = NoesisViolet,
    onPrimary           = Color.White,
    primaryContainer    = NoesisVioletVeil,
    onPrimaryContainer  = NoesisViolet,

    secondary           = NoesisIvory,
    onSecondary         = Color.White,
    secondaryContainer  = NoesisPanelMid,
    onSecondaryContainer = NoesisBone,

    tertiary            = NoesisResolved,
    onTertiary          = Color.White,
    tertiaryContainer   = NoesisArchivedDim,
    onTertiaryContainer = NoesisResolved,

    error               = NoesisWarning,
    onError             = Color.White,
    errorContainer      = NoesisWarningVeil,
    onErrorContainer    = NoesisWarning,

    background          = NoesisVoid,
    onBackground        = NoesisBone,
    surface             = NoesisPanel,
    onSurface           = NoesisBone,
    surfaceVariant      = NoesisPanelMid,
    onSurfaceVariant    = NoesisIvory,

    outline             = BorderLight,
    outlineVariant      = BorderFaint,
    scrim               = Color.Black
)

@Composable
fun NoesisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NoesisColorScheme,
        content = content
    )
}
