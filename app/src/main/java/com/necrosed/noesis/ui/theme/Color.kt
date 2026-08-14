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
// Brighter Foundation (White and Blue) as requested.

val NoesisVoid       = Color(0xFFF8FAFC)   // Primary bg — near-white with blue tint
val NoesisPanel      = Color(0xFFFFFFFF)   // Card surface — pure white
val NoesisPanelMid   = Color(0xFFF1F5F9)   // Mid surface — light gray-blue
val NoesisPanelHigh  = Color(0xFFE2E8F0)   // High surface — darker gray-blue

// ─── BONE / IVORY (Primary Text) ────────────────────────────────
// High Contrast Text

val NoesisBone       = Color(0xFF0F172A)   // Primary text — deep navy/black
val NoesisIvory      = Color(0xFF334155)   // Secondary text — slate gray
val NoesisGray       = Color(0xFF64748B)   // Tertiary text — light slate
val NoesisGrayDim    = Color(0xFF94A3B8)   // Dim text
val NoesisGhostText  = Color(0xFFCBD5E1)   // Ghost text

// ─── VIOLET (Cognitive Significance) ────────────────────────────
// Using Blue as the primary accent

val NoesisViolet     = Color(0xFF2563EB)   // Primary Blue
val NoesisVioletHi   = Color(0xFF3B82F6)   // Bright Blue
val NoesisVioletDim  = Color(0xFF60A5FA)   // Dim Blue
val NoesisVioletVeil = Color(0xFFDBEAFE)   // Very light Blue tint

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
