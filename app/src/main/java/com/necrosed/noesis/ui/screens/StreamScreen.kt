package com.necrosed.noesis.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.necrosed.noesis.data.model.*
import com.necrosed.noesis.ui.MainViewModel
import com.necrosed.noesis.ui.components.*
import com.necrosed.noesis.ui.theme.*

// ═══════════════════════════════════════════════════════════════
// STREAM SCREEN — CHRONOLOGICAL ARCHIVE
// Full entry list with search. Tap an entry for detail view.
// ═══════════════════════════════════════════════════════════════

@Composable
fun StreamScreen(viewModel: MainViewModel) {
    val entries       by viewModel.entries.collectAsStateWithLifecycle()
    val searchQuery   by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedEntry by viewModel.selectedEntry.collectAsStateWithLifecycle()

    if (selectedEntry != null) {
        EntryDetailPanel(
            entry     = selectedEntry!!,
            revisions = viewModel.selectedEntryRevisions.collectAsStateWithLifecycle().value,
            onClose   = viewModel::clearSelectedEntry,
            onToggleUnresolved = { viewModel.toggleUnresolved(selectedEntry!!.entryNumber) },
            onArchive = { viewModel.archiveEntry(selectedEntry!!.entryNumber) },
            onPurge   = { viewModel.purgeEntry(selectedEntry!!.entryNumber) },
            onRevise  = { newContent -> viewModel.reviseEntry(selectedEntry!!.entryNumber, newContent) }
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().noesisScanlines(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            NoesisSectionHeader(
                title    = "ARCHIVE STREAM",
                subtitle = "${entries.size} RECORDS"
            )
        }

        // Search field
        item {
            SearchField(
                query    = searchQuery,
                onQuery  = viewModel::setSearchQuery,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        if (entries.isEmpty()) {
            item {
                NoesisEmptyState(
                    title    = "ARCHIVE EMPTY",
                    subtitle = "No records match the current query.\nCapture a thought to begin."
                )
            }
        } else {
            items(entries, key = { it.entryNumber }) { entry ->
                Box(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                    EntryCard(
                        entry   = entry,
                        onClick = { viewModel.openEntry(entry.entryNumber) }
                    )
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

// ─── SEARCH FIELD ───────────────────────────────────────────────

@Composable
private fun SearchField(query: String, onQuery: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("SEARCH", style = NoesisMicro.copy(color = NoesisGrayDim, letterSpacing = 2.sp))
        Text("·", style = NoesisMicro.copy(color = BorderMid))
        BasicTextField(
            value         = query,
            onValueChange = onQuery,
            modifier      = Modifier.weight(1f),
            textStyle     = NoesisEntryBody.copy(fontSize = 12.sp),
            cursorBrush   = SolidColor(NoesisViolet),
            singleLine    = true,
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("_", style = NoesisMicro.copy(color = NoesisGhostText))
                }
                inner()
            }
        )
        if (query.isNotEmpty()) {
            Text(
                text  = "✕",
                style = NoesisMicro.copy(color = NoesisGrayDim),
                modifier = Modifier.clickable { onQuery("") }
            )
        }
    }
}

// ─── ENTRY DETAIL PANEL ─────────────────────────────────────────

@Composable
private fun EntryDetailPanel(
    entry: Entry,
    revisions: List<Revision>,
    onClose: () -> Unit,
    onToggleUnresolved: () -> Unit,
    onArchive: () -> Unit,
    onPurge: () -> Unit,
    onRevise: (String) -> Unit
) {
    var showReviseMode by remember { mutableStateOf(false) }
    var showPurgeConfirm by remember { mutableStateOf(false) }
    var reviseText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().noesisScanlines(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            // Back + header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = "← STREAM",
                    style = NoesisMicro.copy(color = NoesisVioletDim),
                    modifier = Modifier.clickable { onClose() }
                )
                Text(entry.displayId, style = NoesisMeta.copy(color = NoesisVioletDim))
            }
            NoesisDivider()
        }

        // Entry content
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatFullDate(entry.createdAt), style = NoesisMicro)
                    if (entry.isUnresolved) {
                        Text(
                            text  = "OPEN",
                            style = NoesisBadge.copy(color = NoesisWarning),
                            modifier = Modifier
                                .border(Dp(0.5f), NoesisWarning.copy(alpha = 0.4f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))

                // The sacred original text
                if (!showReviseMode) {
                    Text(text = entry.content, style = NoesisEntryBody)
                } else {
                    // Revision mode
                    if (reviseText.isEmpty()) { reviseText = entry.content }
                    BasicTextField(
                        value       = reviseText,
                        onValueChange = { reviseText = it },
                        textStyle   = NoesisEntryBody,
                        cursorBrush = SolidColor(NoesisViolet),
                        modifier    = Modifier
                            .fillMaxWidth()
                            .border(Dp(0.5f), NoesisVioletDim.copy(alpha = 0.4f))
                            .padding(10.dp)
                    )
                }
            }
        }

        // Concept links
        if (entry.conceptLinks.isNotEmpty()) {
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    NoesisSectionHeader(title = "CONCEPT LINKS")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        entry.conceptLinks.forEach { link ->
                            ConceptChip(label = link.label)
                        }
                    }
                }
            }
        }

        // Revision history
        if (revisions.isNotEmpty()) {
            item {
                NoesisSectionHeader(
                    title    = "REVISION HISTORY",
                    subtitle = "${revisions.size} PRIOR VERSIONS"
                )
            }
            items(revisions) { revision ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(NoesisPanelMid)
                        .border(Dp(0.5f), BorderFaint)
                        .padding(12.dp)
                ) {
                    Text(
                        "REVISION ${revision.revisionNumber.toString().padStart(2, '0')}  " +
                            formatFullDate(revision.createdAt),
                        style = NoesisMicro
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text  = revision.content,
                        style = NoesisEntryBody.copy(color = NoesisGray, fontSize = 11.sp)
                    )
                }
            }
        }

        // Action bar
        item {
            Spacer(Modifier.height(8.dp))
            NoesisDivider()
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!showReviseMode) {
                        NoesisButton(
                            label   = "REVISE",
                            onClick = { showReviseMode = true; reviseText = entry.content },
                            color   = NoesisIvory,
                            modifier = Modifier.weight(1f)
                        )
                        NoesisButton(
                            label   = if (entry.isUnresolved) "MARK RESOLVED" else "MARK OPEN",
                            onClick = onToggleUnresolved,
                            color   = if (entry.isUnresolved) NoesisResolved else NoesisWarning,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        NoesisButton(
                            label   = "SAVE REVISION",
                            onClick = { onRevise(reviseText); showReviseMode = false },
                            color   = NoesisViolet,
                            modifier = Modifier.weight(1f)
                        )
                        NoesisButton(
                            label   = "CANCEL",
                            onClick = { showReviseMode = false },
                            color   = NoesisGrayDim,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Destructive actions
                if (!showReviseMode) {
                    NoesisDottedRule()
                    if (!showPurgeConfirm) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NoesisButton(
                                label   = "ARCHIVE RECORD",
                                onClick = onArchive,
                                color   = NoesisGrayDim,
                                modifier = Modifier.weight(1f)
                            )
                            NoesisButton(
                                label   = "PURGE PERMANENTLY",
                                onClick = { showPurgeConfirm = true },
                                color   = NoesisWarning,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        // Purge confirmation
                        Text(
                            text  = "PURGE PERMANENTLY — ${entry.displayId} will be removed.\nThe entry number is retired forever. This cannot be undone.",
                            style = NoesisMicro.copy(color = NoesisWarning),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NoesisButton(
                                label   = "CANCEL",
                                onClick = { showPurgeConfirm = false },
                                color   = NoesisGrayDim,
                                modifier = Modifier.weight(1f)
                            )
                            NoesisButton(
                                label   = "CONFIRM PURGE",
                                onClick = { onPurge(); showPurgeConfirm = false },
                                color   = NoesisWarning,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
