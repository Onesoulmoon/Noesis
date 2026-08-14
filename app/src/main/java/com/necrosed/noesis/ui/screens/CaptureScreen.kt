package com.necrosed.noesis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.necrosed.noesis.data.model.*
import com.necrosed.noesis.ui.*
import com.necrosed.noesis.ui.components.*
import com.necrosed.noesis.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// CAPTURE SCREEN — PRIMARY HOME SURFACE
//
// The archive's front door. Open → type → archive.
// The screen earns complexity as the archive grows:
//
//   NASCENT    → pure capture terminal, nothing else
//   DEVELOPING → capture + last 5 entries
//   MATURE     → capture + recent + detected patterns panel
// ═══════════════════════════════════════════════════════════════

@Composable
fun CaptureScreen(viewModel: MainViewModel) {
    val captureText   by viewModel.captureText.collectAsStateWithLifecycle()
    val captureStatus by viewModel.captureStatus.collectAsStateWithLifecycle()
    val maturity      by viewModel.archiveMaturity.collectAsStateWithLifecycle()
    val recentEntries by viewModel.recentEntries.collectAsStateWithLifecycle()
    val significant   by viewModel.significantConcepts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .noesisScanlines()
    ) {
        // ── WORDMARK ────────────────────────────────────────────
        ArchiveWordmark()

        // ── CAPTURE TERMINAL ────────────────────────────────────
        CaptureTerminal(
            text     = captureText,
            status   = captureStatus,
            onType   = viewModel::onCaptureTextChange,
            onArchive = viewModel::archiveThought,
            onClear  = viewModel::clearCaptureStatus
        )

        // ── PATTERN PANEL (MATURE only) ──────────────────────────
        AnimatedVisibility(visible = maturity == ArchiveMaturity.MATURE) {
            Column {
                Spacer(Modifier.height(4.dp))
                PatternPanel(concepts = significant)
            }
        }

        // ── RECENT STREAM (DEVELOPING + MATURE) ─────────────────
        AnimatedVisibility(visible = maturity != ArchiveMaturity.NASCENT) {
            Column {
                Spacer(Modifier.height(4.dp))
                RecentStreamPanel(entries = recentEntries)
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

// ─── WORDMARK ───────────────────────────────────────────────────

@Composable
private fun ArchiveWordmark() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Spectral serif title — the NOESIS identity
        Text(
            text  = "NOESIS",
            style = NoesisWordmark
        )
        Text(
            text  = "COGNITIVE ARCHIVE",
            style = NoesisSectionHeader.copy(
                color = NoesisGrayDim,
                letterSpacing = 5.sp
            )
        )
        Spacer(Modifier.height(16.dp))
        NoesisDivider()
    }
}

// ─── CAPTURE TERMINAL ───────────────────────────────────────────

@Composable
private fun CaptureTerminal(
    text: String,
    status: CaptureStatus,
    onType: (String) -> Unit,
    onArchive: () -> Unit,
    onClear: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Prompt label — serif
        Text(
            text  = "Capture",
            style = NoesisConceptSub.copy(color = NoesisVioletDim)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text  = "What is on your mind?",
            style = NoesisMeta.copy(color = NoesisGrayDim)
        )
        Spacer(Modifier.height(12.dp))

        // Input field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NoesisPanel)
                .border(0.5.dp, NoesisVioletDim.copy(alpha = 0.5f))
                .manuscriptCorners(color = NoesisViolet.copy(alpha = 0.6f), size = 10.dp)
                .padding(12.dp)
                .clickable { focusRequester.requestFocus() }
        ) {
            Row(verticalAlignment = Alignment.Top) {
                // Prompt character
                Text(
                    text  = ">",
                    style = NoesisInput.copy(color = NoesisVioletDim),
                    modifier = Modifier.padding(end = 8.dp, top = 1.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = text,
                        onValueChange = onType,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = NoesisInput,
                        cursorBrush = SolidColor(NoesisViolet),
                        minLines = 3,
                        maxLines = 10,
                        decorationBox = { inner ->
                            if (text.isEmpty()) {
                                Text(
                                    text  = "_",
                                    style = NoesisInput.copy(color = NoesisGhostText)
                                )
                            }
                            inner()
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Status + action row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status display
            AnimatedContent(targetState = status, label = "capture_status") { s ->
                when (s) {
                    is CaptureStatus.Idle      -> {
                        Text(
                            text  = "${text.trim().split(Regex("\\s+"))
                                .filter { it.isNotBlank() }.size} TOKENS",
                            style = NoesisMicro.copy(color = NoesisGhostText)
                        )
                    }
                    is CaptureStatus.Archiving -> {
                        Text("ARCHIVING...", style = NoesisMicro.copy(color = NoesisVioletDim))
                    }
                    is CaptureStatus.Archived  -> {
                        Text(
                            text  = "${s.entryId} ARCHIVED",
                            style = NoesisMicro.copy(color = NoesisViolet)
                        )
                        LaunchedEffect(s) {
                            kotlinx.coroutines.delay(2500)
                            onClear()
                        }
                    }
                    is CaptureStatus.Error     -> {
                        Text(s.message, style = NoesisMicro.copy(color = NoesisWarning))
                    }
                }
            }

            // Archive button
            NoesisButton(
                label   = "ARCHIVE",
                onClick = onArchive,
                enabled = text.trim().length >= 3 && status !is CaptureStatus.Archiving,
                color   = NoesisViolet
            )
        }
    }
}

// ─── PATTERN PANEL ──────────────────────────────────────────────

@Composable
private fun PatternPanel(concepts: List<Concept>) {
    if (concepts.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(8.dp))
        NoesisDivider()
        Spacer(Modifier.height(8.dp))

        // Italic serif label — philosophical register
        Text(
            text  = "Detected patterns",
            style = NoesisConceptSub.copy(color = NoesisVioletDim, fontSize = 12.sp)
        )
        Spacer(Modifier.height(10.dp))

        concepts.forEach { concept ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = concept.label,
                    style = NoesisConceptTitle.copy(
                        color    = persistenceLevelColor(concept.persistenceLevel),
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "${concept.observationCount} obs",
                        style = NoesisMicro.copy(color = NoesisGrayDim)
                    )
                    PersistenceBadge(concept.persistenceLevel)
                }
            }
        }
    }
}

// ─── RECENT STREAM PANEL ────────────────────────────────────────

@Composable
private fun RecentStreamPanel(entries: List<Entry>) {
    if (entries.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(8.dp))
        NoesisDivider()
        Spacer(Modifier.height(8.dp))

        Text(
            text  = "RECENT RECORDS",
            style = NoesisSectionHeader
        )
        Spacer(Modifier.height(8.dp))

        entries.forEach { entry ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text  = entry.displayId,
                    style = NoesisMeta.copy(color = NoesisVioletDim),
                    modifier = Modifier.width(50.dp)
                )
                Text(
                    text  = formatEntryDate(entry.createdAt),
                    style = NoesisMicro.copy(color = NoesisGrayDim),
                    modifier = Modifier.width(60.dp)
                )
                Text(
                    text     = entry.content,
                    style    = NoesisMicro.copy(color = NoesisGray),
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                if (entry.isUnresolved) {
                    Text(
                        text  = "◌",
                        style = NoesisMicro.copy(color = NoesisWarning)
                    )
                }
            }
        }
    }
}
