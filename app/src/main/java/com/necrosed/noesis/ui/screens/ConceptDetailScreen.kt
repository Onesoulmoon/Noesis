package com.necrosed.noesis.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.necrosed.noesis.data.model.*
import com.necrosed.noesis.ui.components.*
import com.necrosed.noesis.ui.theme.*
import kotlin.math.roundToInt

// ═══════════════════════════════════════════════════════════════
// CONCEPT DETAIL SCREEN
//
// The full dossier for a single detected concept.
//
// Structure:
//   Identity block     — concept ID, label (serif), persistence
//   Temporal record    — first/last observed, span, recent count
//   Match basis        — how the engine identified this concept
//   Source entries     — the actual N-XXXX records that contain it
//   Related concepts   — co-occurrence-based connections
// ═══════════════════════════════════════════════════════════════

@Composable
fun ConceptDetailScreen(
    concept: Concept,
    sourceEntries: List<Entry>,
    onClose: () -> Unit
) {
    val accentColor = persistenceLevelColor(concept.persistenceLevel)

    LazyColumn(
        modifier = Modifier.fillMaxSize().noesisScanlines(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // ── BACK NAV ────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = "← CONCEPTS",
                    style = NoesisMicro.copy(color = NoesisVioletDim),
                    modifier = Modifier.clickable { onClose() }
                )
                Text(concept.displayId, style = NoesisMeta.copy(color = NoesisVioletDim))
            }
            NoesisDivider()
        }

        // ── IDENTITY BLOCK ──────────────────────────────────────
        item {
            ConceptIdentityBlock(concept = concept, accentColor = accentColor)
        }

        // ── TEMPORAL RECORD ─────────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "TEMPORAL RECORD",
                subtitle = "OBSERVATION TIMELINE"
            )
        }
        item {
            Box(Modifier.padding(horizontal = 12.dp)) {
                TemporalRecord(concept = concept, accentColor = accentColor)
            }
        }

        // ── MATCH BASIS ─────────────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "MATCH BASIS",
                subtitle = "HOW THIS CONCEPT WAS IDENTIFIED"
            )
        }
        item {
            Box(Modifier.padding(horizontal = 12.dp)) {
                MatchBasisPanel(concept = concept)
            }
        }

        // ── SOURCE ENTRIES ──────────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "SOURCE RECORDS",
                subtitle = "${sourceEntries.size} ENTRIES"
            )
        }

        if (sourceEntries.isEmpty()) {
            item {
                NoesisEmptyState(
                    title    = "NO SOURCE RECORDS",
                    subtitle = "Entries are loading or have been purged."
                )
            }
        } else {
            items(sourceEntries, key = { it.entryNumber }) { entry ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    SourceEntryRow(entry = entry, accentColor = accentColor)
                }
            }
        }

        // ── RELATED CONCEPTS ────────────────────────────────────
        if (concept.relatedConcepts.isNotEmpty()) {
            item {
                NoesisSectionHeader(
                    title    = "RELATED CONCEPTS",
                    subtitle = "CO-OCCURRENCE STRENGTH"
                )
            }
            items(concept.relatedConcepts) { related ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    RelatedConceptRow(related = related)
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─── IDENTITY BLOCK ─────────────────────────────────────────────

@Composable
private fun ConceptIdentityBlock(concept: Concept, accentColor: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (concept.persistenceLevel == PLevel.DEEPLY_PERSISTENT)
                    NoesisVioletVeil.copy(alpha = 0.5f) else NoesisPanel
            )
            .border(Dp(0.5f), accentColor.copy(alpha = 0.3f))
            .manuscriptCorners(color = accentColor.copy(alpha = 0.5f), size = 12.dp)
            .padding(16.dp)
    ) {
        Column {
            // Concept label in Spectral serif — the conceptual heading
            Text(
                text  = concept.label,
                style = NoesisConceptTitle.copy(
                    color    = NoesisBone,
                    fontSize = 22.sp
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = concept.displayId,
                style = NoesisMeta.copy(color = accentColor)
            )
            Spacer(Modifier.height(10.dp))
            NoesisDottedRule()
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    ConceptStrengthBar(
                        value = concept.observationCount.coerceAtMost(20),
                        max   = 20,
                        color = accentColor,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = "${concept.observationCount} OBSERVATIONS",
                        style = NoesisMicro.copy(color = NoesisGrayDim)
                    )
                }
                if (concept.persistenceLevel != PLevel.NONE) {
                    PersistenceBadge(concept.persistenceLevel)
                }
            }

            // Surface forms
            if (concept.surfaceForms.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text  = "FORMS  " + concept.surfaceForms
                        .take(6)
                        .joinToString(" · ") { it.lowercase() },
                    style = NoesisMicro.copy(color = NoesisGrayDim)
                )
            }
        }
    }
}

// ─── TEMPORAL RECORD ────────────────────────────────────────────

@Composable
private fun TemporalRecord(concept: Concept, accentColor: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ArchiveStatRow(
                label = "FIRST OBSERVED",
                value = formatFullDate(concept.firstObserved)
            )
            ArchiveStatRow(
                label = "LAST OBSERVED",
                value = formatFullDate(concept.lastObserved)
            )
            ArchiveStatRow(
                label = "SPAN",
                value = "${concept.spanDays} DAYS"
            )
            ArchiveStatRow(
                label = "RECENT (14D)",
                value = "${concept.recentCount14d} OBS"
            )
            ArchiveStatRow(
                label = "CONFIDENCE",
                value = "${concept.confidence}%"
            )
            ArchiveStatRow(
                label = "LANGUAGE",
                value = concept.language.uppercase()
            )
        }
    }
}

// ─── MATCH BASIS ────────────────────────────────────────────────

@Composable
private fun MatchBasisPanel(concept: Concept) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanelMid)
            .border(Dp(0.5f), BorderFaint)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text  = "This concept was identified through the following mechanism:",
                style = NoesisMicro.copy(color = NoesisGrayDim)
            )
            Spacer(Modifier.height(2.dp))
            NoesisDottedRule()
            Spacer(Modifier.height(2.dp))

            Text("STEM NORMALIZATION", style = NoesisLabel)
            val formsDisplay = concept.surfaceForms
                .take(4)
                .joinToString("  →  ") { it.lowercase() }
            val stemDisplay  = "→  ${concept.stem}"
            Text(
                text  = "$formsDisplay  $stemDisplay",
                style = NoesisMicro.copy(color = NoesisIvory)
            )

            Spacer(Modifier.height(4.dp))
            Text("PERSISTENCE CRITERIA", style = NoesisLabel)

            val persistLines = listOf(
                "OBSERVATIONS  ${concept.observationCount}" to
                    (concept.observationCount >= 3),
                "SPAN          ${concept.spanDays}d (threshold: 7d)" to
                    (concept.spanDays >= 7),
                "RECENT (14D)  ${concept.recentCount14d}" to
                    (concept.recentCount14d >= 2)
            )
            persistLines.forEach { (line, met) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = if (met) "✓" else "·",
                        style = NoesisMicro.copy(
                            color = if (met) NoesisViolet else NoesisGhostText
                        )
                    )
                    Text(line, style = NoesisMicro.copy(color = NoesisGray))
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text  = "ASSESSED LEVEL  ${concept.persistenceLevel.displayName}",
                style = NoesisMicro.copy(color = persistenceLevelColor(concept.persistenceLevel))
            )
        }
    }
}

// ─── SOURCE ENTRY ROW ───────────────────────────────────────────

@Composable
private fun SourceEntryRow(entry: Entry, accentColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderFaint)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text  = entry.displayId,
            style = NoesisMeta.copy(color = accentColor),
            modifier = Modifier.width(52.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = entry.content,
                style    = NoesisEntryBody.copy(fontSize = 11.sp),
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(formatFullDate(entry.createdAt), style = NoesisMicro)
        }
        if (entry.isUnresolved) {
            Text("◌", style = NoesisMicro.copy(color = NoesisWarning))
        }
    }
}

// ─── RELATED CONCEPT ROW ────────────────────────────────────────

@Composable
private fun RelatedConceptRow(related: RelatedConcept) {
    val pct = (related.coOccurrenceStrength * 100).roundToInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderFaint)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text  = related.label,
            style = NoesisConceptTitle.copy(color = NoesisBone, fontSize = 13.sp),
            modifier = Modifier.weight(1f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConceptStrengthBar(
                value = pct,
                max   = 100,
                color = NoesisVioletDim
            )
            Text(
                text  = "$pct%",
                style = NoesisMicro.copy(color = NoesisGrayDim),
                modifier = Modifier.width(28.dp)
            )
        }
    }
}
