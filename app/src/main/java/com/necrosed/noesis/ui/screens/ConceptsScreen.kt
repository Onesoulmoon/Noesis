package com.necrosed.noesis.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.necrosed.noesis.data.model.*
import com.necrosed.noesis.ui.MainViewModel
import com.necrosed.noesis.ui.components.*
import com.necrosed.noesis.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// CONCEPTS SCREEN — NOETIC ANALYSIS
//
// The machine's interpretation layer. Ranked by observation count
// within the selected analysis window. Tap a concept to open
// its full dossier with source entries and related concepts.
// ═══════════════════════════════════════════════════════════════

@Composable
fun ConceptsScreen(viewModel: MainViewModel) {
    val concepts        by viewModel.concepts.collectAsStateWithLifecycle()
    val selectedConcept by viewModel.selectedConcept.collectAsStateWithLifecycle()
    val window          by viewModel.window.collectAsStateWithLifecycle()
    val stats           by viewModel.stats.collectAsStateWithLifecycle()

    if (selectedConcept != null) {
        ConceptDetailScreen(
            concept        = selectedConcept!!,
            sourceEntries  = viewModel.selectedConceptEntries.collectAsStateWithLifecycle().value,
            onClose        = viewModel::clearSelectedConcept
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().noesisScanlines(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // ── HEADER ──────────────────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "NOETIC ANALYSIS",
                subtitle = "${concepts.size} CONCEPTS IDENTIFIED"
            )
        }

        // ── WINDOW SELECTOR ─────────────────────────────────────
        item {
            WindowSelector(
                current   = window,
                onSelect  = viewModel::setWindow,
                modifier  = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // ── STATS SUMMARY ───────────────────────────────────────
        stats?.let { s ->
            item {
                Box(Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                    AnalysisSummaryCard(stats = s, window = window)
                }
            }
        }

        // ── PERSISTENCE FILTER TABS ──────────────────────────────
        item {
            Spacer(Modifier.height(4.dp))
            NoesisSectionHeader(
                title    = "RECURRING SUBJECTS",
                subtitle = "ORDERED BY OBSERVATION FREQUENCY"
            )
        }

        // ── CONCEPT LIST ────────────────────────────────────────
        if (concepts.isEmpty()) {
            item {
                NoesisEmptyState(
                    title    = "NO CONCEPTS DETECTED",
                    subtitle = "Archive more thoughts.\nPatterns emerge from accumulated material."
                )
            }
        } else {
            itemsIndexed(concepts, key = { _, c -> c.conceptNumber }) { index, concept ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    ConceptCard(
                        concept = concept,
                        rank    = index + 1,
                        onClick = { viewModel.openConcept(concept.conceptNumber) }
                    )
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─── WINDOW SELECTOR ────────────────────────────────────────────

@Composable
private fun WindowSelector(
    current: AnalysisWindow,
    onSelect: (AnalysisWindow) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(AnalysisWindow.entries) { window ->
            val isActive = window == current
            val color    = if (isActive) NoesisViolet else NoesisGrayDim
            Text(
                text     = window.label,
                style    = NoesisBadge.copy(color = color, letterSpacing = 1.sp),
                modifier = Modifier
                    .border(Dp(0.5f), color.copy(alpha = if (isActive) 0.8f else 0.3f))
                    .background(if (isActive) NoesisVioletVeil else NoesisVoid)
                    .clickable { onSelect(window) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// ─── ANALYSIS SUMMARY CARD ──────────────────────────────────────

@Composable
private fun AnalysisSummaryCard(stats: ArchiveStats, window: AnalysisWindow) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .manuscriptCorners()
    ) {
        Column(Modifier.padding(14.dp)) {
            // Serif heading for the analysis block
            Text(
                text  = "Archive State",
                style = NoesisConceptSub.copy(color = NoesisVioletDim, fontSize = 13.sp)
            )
            Spacer(Modifier.height(10.dp))
            NoesisDottedRule()
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryCell(
                    value = stats.totalEntries.toString(),
                    label = "ACTIVE",
                    color = NoesisBone
                )
                SummaryCell(
                    value = stats.persistentConceptCount.toString(),
                    label = "PERSIST",
                    color = NoesisViolet
                )
                SummaryCell(
                    value = stats.unresolvedCount.toString(),
                    label = "UNRESOLVED",
                    color = NoesisWarning
                )
                SummaryCell(
                    value = stats.totalConceptCount.toString(),
                    label = "CONCEPTS",
                    color = NoesisIvory
                )
            }

            stats.oldestEntryMs?.let { oldest ->
                Spacer(Modifier.height(10.dp))
                NoesisDottedRule()
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = "ARCHIVE ORIGIN  ${formatFullDate(oldest)}",
                    style = NoesisMicro.copy(color = NoesisGhostText)
                )
            }
        }
    }
}

@Composable
private fun SummaryCell(value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = NoesisData.copy(color = color, fontSize = 20.sp))
        Text(label, style = NoesisMicro.copy(color = NoesisGrayDim))
    }
}
