package com.necrosed.noesis.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.necrosed.noesis.ui.components.*
import com.necrosed.noesis.ui.screens.*
import com.necrosed.noesis.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// MAIN SCREEN — NAVIGATION SCAFFOLD
//
// Four tabs:
//   CAPTURE  — the primary home surface (capture terminal)
//   STREAM   — chronological entry archive
//   CONCEPTS — noetic analysis / concept registry
//   ARCHIVE  — configuration and administrative view
//
// The header is minimal — NOESIS is not a surveillance terminal.
// It does not broadcast its own activity level.
// ═══════════════════════════════════════════════════════════════

enum class NoesisTab(val label: String, val symbol: String) {
    CAPTURE  ("CAPTURE",  "◈"),
    STREAM   ("STREAM",   "≡"),
    CONCEPTS ("CONCEPTS", "◎"),
    ARCHIVE  ("ARCHIVE",  "◷"),
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var activeTab by remember { mutableStateOf(NoesisTab.CAPTURE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NoesisVoid)
    ) {
        if (isLoading) {
            NoesisLoading(Modifier.fillMaxSize())
        } else {
            Column(modifier = Modifier.fillMaxSize()) {

                // Minimal header — only show context when not on CAPTURE
                if (activeTab != NoesisTab.CAPTURE) {
                    NoesisTopBar(tab = activeTab, viewModel = viewModel)
                }

                // Content
                Box(modifier = Modifier.weight(1f)) {
                    when (activeTab) {
                        NoesisTab.CAPTURE  -> CaptureScreen(viewModel)
                        NoesisTab.STREAM   -> StreamScreen(viewModel)
                        NoesisTab.CONCEPTS -> ConceptsScreen(viewModel)
                        NoesisTab.ARCHIVE  -> ArchiveScreen(viewModel)
                    }
                }

                // Bottom navigation
                NoesisBottomNav(activeTab = activeTab, onTabSelected = { activeTab = it })
            }
        }
    }
}

// ─── TOP BAR ────────────────────────────────────────────────────

@Composable
private fun NoesisTopBar(tab: NoesisTab, viewModel: MainViewModel) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisVoid)
            .drawBehind {
                drawLine(
                    color       = BorderLight,
                    start       = Offset(0f, size.height),
                    end         = Offset(size.width, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App wordmark — Spectral, condensed in bar context
            Text(
                text  = "NOESIS",
                style = NoesisWordmark.copy(fontSize = 16.sp, letterSpacing = 3.sp)
            )

            // Quiet stats in the header — not prominent
            stats?.let { s ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeaderStat(value = s.totalEntries.toString(), label = "REC")
                    HeaderStat(
                        value = s.persistentConceptCount.toString(),
                        label = "PST",
                        highlight = s.persistentConceptCount > 0
                    )
                    if (s.unresolvedCount > 0) {
                        HeaderStat(
                            value     = s.unresolvedCount.toString(),
                            label     = "OPN",
                            highlight = true,
                            color     = NoesisWarning
                        )
                    }
                }
            }
        }
        NoesisDivider(color = BorderFaint)
    }
}

@Composable
private fun HeaderStat(
    value: String,
    label: String,
    highlight: Boolean = false,
    color: androidx.compose.ui.graphics.Color = NoesisViolet
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text  = value,
            style = NoesisMeta.copy(
                color    = if (highlight) color else NoesisGrayDim,
                fontSize = 11.sp
            )
        )
        Text(label, style = NoesisMicro.copy(color = NoesisGhostText, fontSize = 8.sp))
    }
}

// ─── BOTTOM NAVIGATION ──────────────────────────────────────────

@Composable
private fun NoesisBottomNav(
    activeTab: NoesisTab,
    onTabSelected: (NoesisTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoesisVoid)
    ) {
        NoesisDivider(color = BorderLight)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NoesisTab.entries.forEach { tab ->
                NavItem(
                    tab       = tab,
                    isActive  = tab == activeTab,
                    onClick   = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(tab: NoesisTab, isActive: Boolean, onClick: () -> Unit) {
    val color = if (isActive) NoesisViolet else NoesisGrayDim

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .drawBehind {
                // Active: top accent line in violet
                if (isActive) {
                    drawLine(
                        color       = NoesisViolet,
                        start       = Offset(0f, 0f),
                        end         = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text  = tab.symbol,
            style = NoesisEntryBody.copy(color = color, fontSize = 15.sp)
        )
        Text(
            text  = tab.label,
            style = NoesisNav.copy(color = color)
        )
    }
}
