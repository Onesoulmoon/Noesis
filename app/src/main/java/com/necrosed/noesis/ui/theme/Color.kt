package com.necrosed.noesis.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// NOESIS — COLOR SYSTEM
// PARADIGM: FORBIDDEN RESEARCH ARCHIVE
//           built by philosophers and engineers
//
// The palette is restrained: black + bone + gray.
// Violet appears only when something is cognitively significant.
// It should feel earned, not decorative.
// ═══════════════════════════════════════════════════════════════

// ─── FOUNDATION ─────────────────────────────────────────────────
// Deeper and cooler than INDEX PROHIBITORUM.
// This archive holds thoughts, not surveillance data.

val NoesisVoid       = Color(0xFF08090C)   // Primary bg — near-black with blue undertone
val NoesisPanel      = Color(0xFF111219)   // Card surface — research notebook cover
val NoesisPanelMid   = Color(0xFF181920)   // Mid surface — lifted panel
val NoesisPanelHigh  = Color(0xFF1E2028)   // High surface — foreground element

// ─── BONE / IVORY (Primary Text) ────────────────────────────────
// The color of aged manuscript paper.
// Warm but not amber — intellectual, not industrial.

val NoesisBone       = Color(0xFFD8D2C4)   // Primary text — old manuscript
val NoesisIvory      = Color(0xFFB8B4A8)   // Secondary text — annotation
val NoesisGray       = Color(0xFF8D8A83)   // Tertiary text — metadata
val NoesisGrayDim    = Color(0xFF5A5850)   // Dim text — footnotes
val NoesisGhostText  = Color(0xFF3A3830)   // Ghost text — watermarks, placeholders

// ─── VIOLET (Cognitive Significance) ────────────────────────────
// The defining accent of NOESIS. Used sparingly.
// Appears on: persistent concepts, active states, entry numbers,
//             the app logo, meaningful data points.
// NOT used on: body text, borders, backgrounds, decoration.

val NoesisViolet     = Color(0xFF8176A8)   // Primary violet — concept significance
val NoesisVioletHi   = Color(0xFFA79BCF)   // Bright violet — active, selected
val NoesisVioletDim  = Color(0xFF4E4870)   // Dim violet — subtle accent
val NoesisVioletVeil = Color(0xFF1A1828)   // Violet tint — persistent concept bg

// ─── WARNING / ALERT ────────────────────────────────────────────
// Used for: unresolved thoughts, important flags, deletions.
// A muted, humanistic red — not industrial alarm.

val NoesisWarning    = Color(0xFFB56B63)   // Warning red-orange
val NoesisWarningDim = Color(0xFF6B3830)   // Dim warning
val NoesisWarningVeil= Color(0xFF1E1010)   // Warning tint

// ─── RESOLUTION / ARCHIVE ───────────────────────────────────────
// Resolved thoughts. Archived records.

val NoesisResolved   = Color(0xFF6A8A7A)   // Muted sage — resolved state
val NoesisArchivedDim= Color(0xFF2A3830)   // Archive tint

// ─── BORDERS ────────────────────────────────────────────────────
val BorderFaint  = Color(0xFF181A20)       // Barely visible
val BorderLight  = Color(0xFF252830)       // Panel dividers
val BorderMid    = Color(0xFF333640)       // Card outlines
val BorderActive = Color(0xFF4A4E60)       // Active / focused

// ─── PERSISTENCE LEVEL COLORS ───────────────────────────────────
// Each persistence level has its own color weight.

val ColorRecurring       = NoesisIvory           // RECURRING — bone, understated
val ColorPersistent      = NoesisViolet          // PERSISTENT — violet emerges
val ColorDeeplyPersistent = NoesisVioletHi       // DEEPLY PERSISTENT — full violet
val ColorUnresolved      = NoesisWarning         // UNRESOLVED — warm warning
val ColorDormant         = NoesisGrayDim         // DORMANT — nearly invisible
