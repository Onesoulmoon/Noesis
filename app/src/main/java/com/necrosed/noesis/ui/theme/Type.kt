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
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Light),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Normal),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.Medium),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = PlexMonoFont, fontProvider = GFontProvider,
        weight = FontWeight.Normal, style = FontStyle.Italic),
)

// ─── TYPOGRAPHY STYLES ──────────────────────────────────────────

val NoesisWordmark = TextStyle(
    fontFamily    = Spectral,
    fontWeight    = FontWeight.SemiBold,
    fontSize      = 28.sp,
    letterSpacing = 1.sp
)

val NoesisSectionHeader = TextStyle(
    fontFamily    = Spectral,
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
    fontFamily    = Spectral,
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
