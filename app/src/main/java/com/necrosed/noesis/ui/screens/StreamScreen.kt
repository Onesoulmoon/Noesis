package com.necrosed.noesis.ui.screens

import androidx.compose.animation.*
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
import com.necrosed.noesis.data.model.Composition
import com.necrosed.noesis.data.model.Entry
import com.necrosed.noesis.data.model.Revision
import com.necrosed.noesis.data.model.ConceptLink
import com.necrosed.noesis.ui.MainViewModel
import com.necrosed.noesis.ui.CompositionStatus
import com.necrosed.noesis.ui.ModelStatus
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
    val composition by viewModel.selectedComposition.collectAsStateWithLifecycle()
    val compositionStatus by viewModel.compositionStatus.collectAsStateWithLifecycle()
    val modelStatus by viewModel.modelStatus.collectAsStateWithLifecycle()

    if (selectedEntry != null) {
        androidx.activity.compose.BackHandler {
            viewModel.clearSelectedEntry()
        }
        EntryDetailPanel(
            entry     = selectedEntry!!,
            revisions = viewModel.selectedEntryRevisions.collectAsStateWithLifecycle().value,
            composition = composition,
            compositionStatus = compositionStatus,
            modelStatus = modelStatus,
            onCompose = { mode -> viewModel.composeSelectedEntry(selectedEntry!!.entryNumber, mode) },
            onInstallLocalModel = viewModel::installLocalModel,
            onSaveComposition = viewModel::saveComposition,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompositionPanel(
    composition: Composition?,
    status: CompositionStatus,
    modelStatus: ModelStatus,
    onCompose: (String) -> Unit,
    onInstallLocalModel: () -> Unit,
    onSaveEdit: (Composition) -> Unit,
    conceptLinks: List<ConceptLink>
) {
    var expandedSectionIndex by remember { mutableIntStateOf(-1) }
    var showRegenMenu by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    
    // Edit buffers
    var editTitle by remember(composition) { mutableStateOf(composition?.title ?: "") }
    var editSubtitle by remember(composition) { mutableStateOf(composition?.subtitle ?: "") }
    var editKeyInsight by remember(composition) { mutableStateOf(composition?.keyInsight ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .padding(16.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("COMPOSED THOUGHT", style = NoesisSectionHeader.copy(color = NoesisViolet))
                Text("ON-DEVICE / GEMMA 4 E2B", style = NoesisMicro.copy(color = NoesisGrayDim, letterSpacing = 1.5.sp))
            }
            
            if (composition != null && !editMode) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "EDIT",
                        style = NoesisMicro.copy(color = NoesisVioletDim),
                        modifier = Modifier.clickable { editMode = true }
                    )
                    Box {
                        Text(
                            text = "REGENERATE ▾",
                            style = NoesisMicro.copy(color = NoesisVioletDim),
                            modifier = Modifier.clickable { showRegenMenu = true }
                        )
                        androidx.compose.material3.DropdownMenu(
                            expanded = showRegenMenu,
                            onDismissRequest = { showRegenMenu = false },
                            modifier = Modifier.background(NoesisPanelHigh).border(Dp(0.5f), BorderLight)
                        ) {
                            listOf(
                                "default" to "Default",
                                "concise" to "More Concise",
                                "analytical" to "More Analytical",
                                "literal" to "More Literal",
                                "reorganize" to "Reorganize"
                            ).forEach { (mode, label) ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text(label, style = NoesisMicro.copy(color = NoesisBone)) },
                                    onClick = { 
                                        showRegenMenu = false
                                        onCompose(mode)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (editMode) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "CANCEL",
                        style = NoesisMicro.copy(color = NoesisGrayDim),
                        modifier = Modifier.clickable { editMode = false }
                    )
                    Text(
                        text = "SAVE",
                        style = NoesisMicro.copy(color = NoesisViolet),
                        modifier = Modifier.clickable { 
                            onSaveEdit(composition!!.copy(
                                title = editTitle,
                                subtitle = editSubtitle.takeIf { it.isNotBlank() },
                                keyInsight = editKeyInsight.takeIf { it.isNotBlank() }
                            ))
                            editMode = false 
                        }
                    )
                }
            }

            if (composition == null) {
                when (status) {
                    is CompositionStatus.Composing -> Text("PROCESSING…", style = NoesisMicro.copy(color = NoesisVioletDim))
                    is CompositionStatus.Error -> Text("ERROR", style = NoesisMicro.copy(color = NoesisWarning))
                    else -> {
                        when (modelStatus) {
                            is ModelStatus.NotInstalled -> NoesisButton("INSTALL LOCAL AI", onInstallLocalModel, enabled = true, color = NoesisViolet)
                            is ModelStatus.Downloading -> Text("INSTALLING ${modelStatus.progress}%", style = NoesisMicro.copy(color = NoesisVioletDim))
                            is ModelStatus.Ready -> NoesisButton("COMPOSE", { onCompose("default") }, enabled = true, color = NoesisViolet)
                            is ModelStatus.Error -> NoesisButton("RETRY INSTALL", onInstallLocalModel, enabled = true, color = NoesisViolet)
                            is ModelStatus.Incompatible -> Text("AI UNAVAILABLE", style = NoesisMicro.copy(color = NoesisWarning))
                            ModelStatus.Checking -> Text("CHECKING AI…", style = NoesisMicro.copy(color = NoesisGrayDim))
                        }
                    }
                }
            }
        }

        if (status is CompositionStatus.Error) {
            Spacer(Modifier.height(8.dp))
            Text(status.message, style = NoesisMicro.copy(color = NoesisWarning))
            Spacer(Modifier.height(8.dp))
            if (modelStatus is ModelStatus.NotInstalled || modelStatus is ModelStatus.Error) {
                NoesisButton("INSTALL / RETRY LOCAL AI", onInstallLocalModel, enabled = true, color = NoesisViolet)
            } else {
                NoesisButton("RETRY", { onCompose("default") }, enabled = true, color = NoesisViolet)
            }
        }

        composition?.let { c ->
            Spacer(Modifier.height(20.dp))
            
            if (!editMode) {
                Text(c.title.uppercase(), style = NoesisWordmark.copy(fontSize = 24.sp, color = NoesisBone))
                c.subtitle?.let { Text(it, style = NoesisConceptSub.copy(color = NoesisIvory)) }
            } else {
                BasicTextField(
                    value = editTitle,
                    onValueChange = { editTitle = it },
                    textStyle = NoesisWordmark.copy(fontSize = 24.sp, color = NoesisBone),
                    modifier = Modifier.fillMaxWidth().border(Dp(0.5f), NoesisVioletDim.copy(alpha = 0.3f)).padding(4.dp)
                )
                Spacer(Modifier.height(4.dp))
                BasicTextField(
                    value = editSubtitle,
                    onValueChange = { editSubtitle = it },
                    textStyle = NoesisConceptSub.copy(color = NoesisIvory),
                    modifier = Modifier.fillMaxWidth().border(Dp(0.5f), NoesisVioletDim.copy(alpha = 0.3f)).padding(4.dp),
                    decorationBox = { inner -> if (editSubtitle.isEmpty()) Text("Subtitle (optional)", style = NoesisConceptSub.copy(color = NoesisGhostText)); inner() }
                )
            }
            
            Spacer(Modifier.height(16.dp))
            NoesisDivider()
            
            c.sections.forEachIndexed { index, section ->
                val isExpanded = expandedSectionIndex == index
                val isTension = section.type == "TENSION"
                val accent = if (isTension) NoesisWarning else NoesisVioletDim
                
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable { expandedSectionIndex = if (isExpanded) -1 else index }
                        .padding(vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(section.title.uppercase(), style = NoesisSectionHeader.copy(fontSize = 14.sp, color = accent, letterSpacing = 1.sp), modifier = Modifier.weight(1f))
                        section.epistemicStatus?.let { status ->
                            Text(
                                text = status,
                                style = NoesisMicro.copy(
                                    color = when(status) {
                                        "FACT" -> NoesisViolet
                                        "BELIEF" -> NoesisResolved
                                        "HYPOTHESIS" -> NoesisVioletDim
                                        "QUESTION" -> NoesisWarning
                                        else -> NoesisGrayDim
                                    },
                                    fontSize = 8.sp,
                                    letterSpacing = 1.sp
                                ),
                                modifier = Modifier
                                    .border(Dp(0.5f), NoesisGrayDim.copy(alpha = 0.3f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(section.content, style = NoesisEntryBody.copy(fontSize = 14.sp, color = NoesisBone))
                    
                    AnimatedVisibility(visible = isExpanded && !editMode) {
                        Column(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .background(if (isTension) NoesisWarningVeil else NoesisPanelMid)
                                .border(Dp(0.5f), accent.copy(alpha = 0.3f))
                                .padding(12.dp)
                        ) {
                            Text(if (isTension) "WHY THIS TENSION?" else "WHY THIS SECTION?", style = NoesisLabel.copy(color = accent, fontSize = 10.sp))
                            Spacer(Modifier.height(8.dp))
                            
                            if (section.sourceFragments.isNotEmpty()) {
                                Text("DERIVED FROM:", style = NoesisMicro.copy(color = NoesisGrayDim))
                                section.sourceFragments.forEach { fragment ->
                                    Text("• \"$fragment\"", style = NoesisEntryBody.copy(fontSize = 12.sp, color = NoesisIvory))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            
                            section.interpretation?.let {
                                Text("INTERPRETATION:", style = NoesisMicro.copy(color = NoesisGrayDim))
                                Text(it, style = NoesisEntryBody.copy(fontSize = 12.sp, color = NoesisBone))
                            }
                        }
                    }
                }
                NoesisDottedRule(color = BorderFaint)
            }
            
            c.keyInsight?.let { insight ->
                Column(Modifier.padding(vertical = 12.dp)) {
                    Text("KEY INSIGHT", style = NoesisSectionHeader.copy(fontSize = 14.sp, color = NoesisViolet, letterSpacing = 1.5.sp))
                    Spacer(Modifier.height(6.dp))
                    if (!editMode) {
                        Text(insight, style = NoesisEntryBody.copy(fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                    } else {
                        BasicTextField(
                            value = editKeyInsight,
                            onValueChange = { editKeyInsight = it },
                            textStyle = NoesisEntryBody.copy(fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth().border(Dp(0.5f), NoesisVioletDim.copy(alpha = 0.3f)).padding(4.dp)
                        )
                    }
                }
                NoesisDivider(color = BorderLight)
            }
            
            if (c.openQuestions.isNotEmpty()) {
                Column(Modifier.padding(vertical = 12.dp)) {
                    Text("OPEN QUESTIONS", style = NoesisSectionHeader.copy(fontSize = 14.sp, color = NoesisVioletDim, letterSpacing = 1.5.sp))
                    for (q in c.openQuestions) {
                        Row(Modifier.padding(top = 8.dp)) {
                            Text("• ", style = NoesisEntryBody.copy(color = NoesisVioletDim))
                            Text(q, style = NoesisEntryBody.copy(fontSize = 13.sp))
                        }
                    }
                }
                NoesisDivider(color = BorderLight)
            }

            if (conceptLinks.isNotEmpty()) {
                Column(Modifier.padding(vertical = 12.dp)) {
                    Text("CONNECTIONS", style = NoesisSectionHeader.copy(fontSize = 14.sp, color = NoesisGray, letterSpacing = 1.5.sp))
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (link in conceptLinks) {
                            ConceptChip(label = link.label)
                        }
                    }
                }
            }
        }
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
    composition: Composition?,
    compositionStatus: CompositionStatus,
    modelStatus: ModelStatus,
    onCompose: (String) -> Unit,
    onInstallLocalModel: () -> Unit,
    onSaveComposition: (Composition) -> Unit,
    onClose: () -> Unit,
    onToggleUnresolved: () -> Unit,
    onArchive: () -> Unit,
    onPurge: () -> Unit,
    onRevise: (String) -> Unit
) {
    var showReviseMode by remember { mutableStateOf(false) }
    var showPurgeConfirm by remember { mutableStateOf(false) }
    var reviseText by remember { mutableStateOf("") }
    var rawDumpExpanded by remember { mutableStateOf(composition == null) }

    if (showReviseMode) {
        androidx.activity.compose.BackHandler {
            showReviseMode = false
        }
    } else if (showPurgeConfirm) {
        androidx.activity.compose.BackHandler {
            showPurgeConfirm = false
        }
    }

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

        // ── COMPOSED THOUGHT ─────────────────────────────────────
        item {
            CompositionPanel(
                composition = composition,
                status = compositionStatus,
                modelStatus = modelStatus,
                onCompose = onCompose,
                onInstallLocalModel = onInstallLocalModel,
                onSaveEdit = onSaveComposition,
                conceptLinks = entry.conceptLinks
            )
        }

        // Entry content (RAW DUMP)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { if (composition != null) rawDumpExpanded = !rawDumpExpanded }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (rawDumpExpanded) "[ RAW DUMP ▾ ]" else "[ RAW DUMP ▸ ]",
                        style = NoesisMicro.copy(color = NoesisGrayDim)
                    )
                }

                AnimatedVisibility(visible = rawDumpExpanded) {
                    Column {
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
                            Text(text = entry.content, style = NoesisEntryBody.copy(color = NoesisIvory))
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
            }
        }

        // Revision history
        if (revisions.isNotEmpty() && rawDumpExpanded) {
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
                            onClick = { showReviseMode = true; reviseText = entry.content; rawDumpExpanded = true },
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
