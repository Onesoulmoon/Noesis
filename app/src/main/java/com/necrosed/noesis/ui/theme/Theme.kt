package com.necrosed.noesis.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NoesisColorScheme = darkColorScheme(
    primary             = NoesisViolet,
    onPrimary           = NoesisVoid,
    primaryContainer    = NoesisVioletVeil,
    onPrimaryContainer  = NoesisVioletHi,

    secondary           = NoesisIvory,
    onSecondary         = NoesisVoid,
    secondaryContainer  = NoesisPanelMid,
    onSecondaryContainer = NoesisBone,

    tertiary            = NoesisResolved,
    onTertiary          = NoesisVoid,
    tertiaryContainer   = NoesisArchivedDim,
    onTertiaryContainer = NoesisResolved,

    error               = NoesisWarning,
    onError             = NoesisBone,
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
    scrim               = NoesisVoid
)

@Composable
fun NoesisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NoesisColorScheme,
        content = content
    )
}
