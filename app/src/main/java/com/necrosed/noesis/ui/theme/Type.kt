package com.necrosed.noesis.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.necrosed.noesis.R

// ═══════════════════════════════════════════════════════════════
// NOESIS — MONOSPACE TYPOGRAPHY SYSTEM
//
// Paradigm: The Research Terminal.
// All content is rendered in IBM PLEX MONO to emphasize that
// every thought is a record, not a manuscript.
// ═══════════════════════════════════════════════════════════════

private val GFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val PlexMonoFont  = GoogleFont("IBM Plex Mono")

val PlexMono = FontFamily(
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Light),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Normal),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Medium),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider,
        weight = FontWeight.Normal, style = FontStyle.Italic),
)

// ─── TYPOGRAPHY STYLES ──────────────────────────────────────────

val NoesisWordmark = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 28.sp,
    letterSpacing = 1.sp
)

val NoesisSectionHeader = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Medium,
    fontSize      = 18.sp,
    letterSpacing = 0.5.sp
)

val NoesisEntryBody = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Normal,
    fontSize      = 15.sp,
    lineHeight    = 22.sp
)

val NoesisInput = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Medium,
    fontSize      = 16.sp,
    lineHeight    = 24.sp
)

val NoesisMeta = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Medium,
    fontSize      = 12.sp,
    letterSpacing = 1.sp
)

val NoesisMicro = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Medium,
    fontSize      = 10.sp,
    letterSpacing = 1.5.sp
)

val NoesisConceptSub = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Medium,
    fontStyle     = FontStyle.Italic,
    fontSize      = 14.sp
)

val NoesisBadge = NoesisMicro
val NoesisLabel = NoesisMeta
val NoesisData = NoesisMeta
val NoesisConceptTitle = NoesisSectionHeader
val NoesisNav = NoesisMeta

val Typography = androidx.compose.material3.Typography(
    bodyLarge = NoesisEntryBody,
    titleLarge = NoesisWordmark,
    labelSmall = NoesisMicro
)
