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
// NOESIS — DUAL TYPOGRAPHY SYSTEM
//
// TWO TYPEFACES. STRICT SEPARATION. NO EXCEPTIONS.
//
// SPECTRAL (serif)
//   Purpose: NOESIS identity, concept headings, section titles,
//            archival quotations, philosophical statements.
//   Character: Scholarly, authoritative, forbidden library.
//   Usage: Sparing. Headlines and conceptual markers only.
//
// IBM PLEX MONO (monospace)
//   Purpose: All records, timestamps, IDs, statistics, labels,
//            system messages, entry text, technical metadata.
//   Character: Research terminal, precise, trustworthy.
//   Usage: Everything else. The default typeface of the archive.
//
// The user's raw thought text should feel like a record,
// not book typography. Monospace for all entry content.
// ═══════════════════════════════════════════════════════════════

private val GFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

private val SpectralFont  = GoogleFont("Spectral")
private val PlexMonoFont  = GoogleFont("IBM Plex Mono")

val Spectral = FontFamily(
    Font(googleFont = SpectralFont, fontProvider = GFontProvider, weight = FontWeight.Normal),
    Font(googleFont = SpectralFont, fontProvider = GFontProvider, weight = FontWeight.Medium),
    Font(googleFont = SpectralFont, fontProvider = GFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = SpectralFont, fontProvider = GFontProvider,
        weight = FontWeight.Normal, style = FontStyle.Italic),
)

val PlexMono = FontFamily(
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Normal),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Medium),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.SemiBold),
)

// ─── SPECTRAL TYPE SCALE ────────────────────────────────────────

// App wordmark / screen identity header
val NoesisWordmark = TextStyle(
    fontFamily = Spectral,
    fontWeight = FontWeight.SemiBold,
    fontSize   = 26.sp,
    lineHeight = 32.sp,
    letterSpacing = 2.sp,
    color = NoesisVioletHi
)

// Concept name, section title — the primary serif heading
val NoesisConceptTitle = TextStyle(
    fontFamily = Spectral,
    fontWeight = FontWeight.Medium,
    fontSize   = 18.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp,
    color = NoesisBone
)

// Sub-heading — archival group headers, category labels
val NoesisConceptSub = TextStyle(
    fontFamily = Spectral,
    fontWeight = FontWeight.Normal,
    fontStyle  = FontStyle.Italic,
    fontSize   = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
    color = NoesisIvory
)

// ─── PLEX MONO TYPE SCALE ───────────────────────────────────────

// Section header — screen-level labels in monospace
val NoesisSectionHeader = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 10.sp,
    lineHeight    = 14.sp,
    letterSpacing = 3.sp,
    color = NoesisGray
)

// Entry body — the user's actual thought text
val NoesisEntryBody = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Normal,
    fontSize      = 13.sp,
    lineHeight    = 20.sp,
    letterSpacing = 0.2.sp,
    color = NoesisBone
)

// Entry ID, timestamp, metadata
val NoesisMeta = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Normal,
    fontSize      = 10.sp,
    lineHeight    = 14.sp,
    letterSpacing = 1.sp,
    color = NoesisGray
)

// Field labels — "OBSERVATIONS", "FIRST OBSERVED", etc.
val NoesisLabel = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Normal,
    fontSize      = 9.sp,
    lineHeight    = 12.sp,
    letterSpacing = 2.sp,
    color = NoesisGrayDim
)

// Data values — numbers, counts, statistics
val NoesisData = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Medium,
    fontSize      = 12.sp,
    lineHeight    = 16.sp,
    letterSpacing = 0.5.sp,
    color = NoesisBone
)

// Persistence badge — [PERSISTENT], [RECURRING] etc.
val NoesisBadge = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 8.sp,
    lineHeight    = 11.sp,
    letterSpacing = 2.sp
)

// Input field — the capture text input
val NoesisInput = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Normal,
    fontSize      = 14.sp,
    lineHeight    = 22.sp,
    letterSpacing = 0.2.sp,
    color = NoesisBone
)

// Navigation labels
val NoesisNav = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Medium,
    fontSize      = 8.sp,
    lineHeight    = 11.sp,
    letterSpacing = 2.sp
)

// Micro / footnote — smallest readable text
val NoesisMicro = TextStyle(
    fontFamily    = PlexMono,
    fontWeight    = FontWeight.Normal,
    fontSize      = 9.sp,
    lineHeight    = 13.sp,
    letterSpacing = 0.5.sp,
    color = NoesisGrayDim
)
