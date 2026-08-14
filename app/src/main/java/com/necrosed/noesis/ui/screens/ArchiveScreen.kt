package com.necrosed.noesis.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
// ARCHIVE SCREEN — CONFIGURATION & ADMINISTRATIVE VIEW
//
// Analysis window selector, archive statistics, persistence
// thresholds, and operational notes. No analytics. No cloud.
// ═══════════════════════════════════════════════════════════════

@Composable
fun ArchiveScreen(viewModel: MainViewModel) {
    val window by viewModel.window.collectAsStateWithLifecycle()
    val stats  by viewModel.stats.collectAsStateWithLifecycle()
    val modelStatus by viewModel.modelStatus.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().noesisScanlines(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // ── ARCHIVE IDENTITY ────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Text("NOESIS", style = NoesisWordmark)
                Text(
                    "COGNITIVE ARCHIVE",
                    style = NoesisSectionHeader.copy(
                        color = NoesisGrayDim, letterSpacing = 4.sp
                    )
                )
                Spacer(Modifier.height(12.dp))
                NoesisDivider()
                Spacer(Modifier.height(10.dp))
                Text(
                    text  = "Version 1.0.0 — REF:NΩ-001\n" +
                        "Local-first. Encrypted. No network. No analytics.",
                    style = NoesisMicro.copy(color = NoesisGhostText)
                )
            }
        }

        // ── LOCAL AI ────────────────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "LOCAL AI",
                subtitle = "PRIVATE ON-DEVICE COMPOSITION ENGINE"
            )
        }
        item {
            Box(Modifier.padding(horizontal = 12.dp)) {
                LocalAiPanel(
                    modelStatus = modelStatus,
                    onInstall = viewModel::installLocalModel
                )
            }
        }

        // ── ARCHIVE STATISTICS ──────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "ARCHIVE STATISTICS",
                subtitle = "CURRENT STATE OF THE COGNITIVE RECORD"
            )
        }
        item {
            Box(Modifier.padding(horizontal = 12.dp)) {
                ArchiveStatsPanel(stats = stats)
            }
        }

        // ── ANALYSIS WINDOW ─────────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "ANALYSIS WINDOW",
                subtitle = "TEMPORAL FRAME FOR PATTERN DETECTION"
            )
        }
        item {
            Box(Modifier.padding(horizontal = 12.dp)) {
                WindowConfigPanel(current = window, onSelect = viewModel::setWindow)
            }
        }

        // ── PERSISTENCE THRESHOLDS ──────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "PERSISTENCE THRESHOLDS",
                subtitle = "CRITERIA FOR CONCEPT CLASSIFICATION"
            )
        }
        item {
            Box(Modifier.padding(horizontal = 12.dp)) {
                ThresholdPanel()
            }
        }

        // ── OPERATIONAL NOTES ───────────────────────────────────
        item {
            NoesisSectionHeader(
                title    = "OPERATIONAL ARCHITECTURE",
                subtitle = "HOW THE ANALYSIS ENGINE WORKS"
            )
        }
        item {
            Box(Modifier.padding(horizontal = 12.dp)) {
                OperationalNotesPanel()
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─── LOCAL AI PANEL ─────────────────────────────────────────────

@Composable
private fun LocalAiPanel(
    modelStatus: com.necrosed.noesis.ui.ModelStatus,
    onInstall: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .manuscriptCorners()
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                "GEMMA 4 E2B",
                style = NoesisConceptSub.copy(color = NoesisVioletDim, letterSpacing = 1.5.sp)
            )
            Text(
                "LOCAL / OFFLINE / NO CLOUD INFERENCE",
                style = NoesisMicro.copy(color = NoesisGhostText, letterSpacing = 1.sp)
            )
            when (modelStatus) {
                com.necrosed.noesis.ui.ModelStatus.Checking ->
                    Text("Checking device compatibility…", style = NoesisMicro.copy(color = NoesisGrayDim))
                com.necrosed.noesis.ui.ModelStatus.Ready -> {
                    Text("● READY", style = NoesisMicro.copy(color = NoesisViolet))
                    Text("Model installed. Thoughts are processed on-device.", style = NoesisMicro.copy(color = NoesisGrayDim))
                }
                is com.necrosed.noesis.ui.ModelStatus.Downloading -> {
                    Text("● DOWNLOADING ${modelStatus.progress}%", style = NoesisMicro.copy(color = NoesisViolet))
                    Box(Modifier.fillMaxWidth().height(3.dp).background(NoesisVoid)) {
                        Box(Modifier.fillMaxWidth(modelStatus.progress / 100f).fillMaxHeight().background(NoesisViolet))
                    }
                    Text("Keep NOESIS open while the model is being installed.", style = NoesisMicro.copy(color = NoesisGrayDim))
                }
                is com.necrosed.noesis.ui.ModelStatus.NotInstalled -> {
                    Text("○ NOT INSTALLED", style = NoesisMicro.copy(color = NoesisGrayDim))
                    Text(
                        "~2.6 GB model · ${modelStatus.ramGb} GB RAM detected · ${modelStatus.freeStorageGb} GB free",
                        style = NoesisMicro.copy(color = NoesisGhostText)
                    )
                    Text("The model is downloaded once and then runs entirely offline.", style = NoesisMicro.copy(color = NoesisGrayDim))
                    NoesisButton("INSTALL GEMMA 4 E2B", onInstall, enabled = true, color = NoesisViolet)
                }
                is com.necrosed.noesis.ui.ModelStatus.Incompatible -> {
                    Text("× DEVICE NOT COMPATIBLE", style = NoesisMicro.copy(color = NoesisWarning))
                    Text(modelStatus.reason, style = NoesisMicro.copy(color = NoesisGrayDim))
                    Text("NOESIS can still capture and analyze thoughts without the local LLM.", style = NoesisMicro.copy(color = NoesisGrayDim))
                }
                is com.necrosed.noesis.ui.ModelStatus.Error -> {
                    Text("× INSTALLATION ERROR", style = NoesisMicro.copy(color = NoesisWarning))
                    Text(modelStatus.message, style = NoesisMicro.copy(color = NoesisGrayDim))
                    NoesisButton("RETRY INSTALL", onInstall, enabled = true, color = NoesisViolet)
                }
            }
        }
    }
}

// ─── ARCHIVE STATS PANEL ────────────────────────────────────────

@Composable
private fun ArchiveStatsPanel(stats: ArchiveStats?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .manuscriptCorners()
            .padding(14.dp)
    ) {
        if (stats == null) {
            Text("Loading...", style = NoesisMicro.copy(color = NoesisGhostText))
            return@Box
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ArchiveStatRow("TOTAL ENTRIES",   stats.totalEntries.toString())
            ArchiveStatRow("UNRESOLVED",      stats.unresolvedCount.toString())
            ArchiveStatRow("TOTAL CONCEPTS",  stats.totalConceptCount.toString())
            ArchiveStatRow("PERSISTENT",      stats.persistentConceptCount.toString())
            stats.oldestEntryMs?.let {
                ArchiveStatRow("ARCHIVE ORIGIN", formatFullDate(it))
            }
            ArchiveStatRow("STORAGE",         "ENCRYPTED · LOCAL ONLY")
            ArchiveStatRow("CIPHER",          "SQLCipher AES-256-CBC")
            ArchiveStatRow("KEY STORAGE",     "Android Keystore")
        }
    }
}

// ─── WINDOW CONFIG PANEL ────────────────────────────────────────

@Composable
private fun WindowConfigPanel(current: AnalysisWindow, onSelect: (AnalysisWindow) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "Entries are permanent. Analysis is temporal.\n" +
                "The same thought can be dormant in 7 days and deeply persistent over 1 year.",
                style = NoesisMicro.copy(color = NoesisGrayDim)
            )
            Spacer(Modifier.height(12.dp))

            AnalysisWindow.entries.forEach { w ->
                val isActive = w == current
                val color    = if (isActive) NoesisViolet else NoesisGray
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isActive) NoesisVioletVeil else NoesisVoid)
                        .border(Dp(0.5f), if (isActive) NoesisVioletDim else BorderFaint)
                        .clickable { onSelect(w) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(w.label, style = NoesisData.copy(color = color))
                    if (isActive) {
                        Text(
                            "ACTIVE",
                            style = NoesisBadge.copy(color = NoesisViolet, letterSpacing = 2.sp),
                            modifier = Modifier
                                .border(Dp(0.5f), NoesisViolet.copy(alpha = 0.4f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

// ─── PERSISTENCE THRESHOLD PANEL ────────────────────────────────

@Composable
private fun ThresholdPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(
                Triple(
                    PLevel.RECURRING,
                    NoesisIvory,
                    "≥ 2 observations in any window"
                ),
                Triple(
                    PLevel.PERSISTENT,
                    NoesisViolet,
                    "≥ 3 observations · ≥ 7 days between first and last"
                ),
                Triple(
                    PLevel.DEEPLY_PERSISTENT,
                    NoesisVioletHi,
                    "≥ 5 observations · ≥ 30 days span · ≥ 2 in last 14 days"
                )
            ).forEach { (level, color, criteria) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text  = level.displayName,
                        style = NoesisBadge.copy(color = color, letterSpacing = 1.sp),
                        modifier = Modifier
                            .border(Dp(0.5f), color.copy(alpha = 0.4f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                    Text(
                        text  = criteria,
                        style = NoesisMicro.copy(color = NoesisGray),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            NoesisDottedRule()
            Text(
                text  = "Thresholds are hardcoded in v1.0. " +
                    "Configurable thresholds are planned for v1.5.",
                style = NoesisMicro.copy(color = NoesisGhostText)
            )
        }
    }
}

// ─── OPERATIONAL NOTES ──────────────────────────────────────────

@Composable
private fun OperationalNotesPanel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                "TOKENIZATION"   to "Text split by whitespace + punctuation",
                "NORMALIZATION"  to "Lowercase, strip punctuation",
                "STOP WORDS"     to "EN + FR stop word lists",
                "STEMMING"       to "Porter (EN) · Snowball subset (FR)",
                "PHRASES"        to "Bigrams + trigrams, min 2 occurrences",
                "MATCHING"       to "Stem + surface form comparison",
                "PERSISTENCE"    to "Temporal analysis across observation history",
                "NETWORK"        to "Zero — local only",
                "AI / LLM"       to "None in v1.0 — engine is deterministic"
            ).forEach { (k, v) ->
                ArchiveStatRow(k, v, modifier = Modifier.padding(vertical = 1.dp))
            }

            Spacer(Modifier.height(4.dp))
            NoesisDottedRule()
            Spacer(Modifier.height(4.dp))

            // Italic serif closing note — philosophical register
            Text(
                text  = "The user's original thought is sacred.\n" +
                    "NOESIS interpretations are metadata layered on top,\n" +
                    "never rewriting the record beneath.",
                style = NoesisConceptSub.copy(
                    color    = NoesisVioletDim,
                    fontSize = 11.sp
                )
            )
        }
    }
}
