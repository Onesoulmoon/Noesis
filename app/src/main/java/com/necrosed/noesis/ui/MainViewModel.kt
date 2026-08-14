package com.necrosed.noesis.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.necrosed.noesis.data.db.NoesisDatabase
import com.necrosed.noesis.ai.OnDeviceModelManager
import com.necrosed.noesis.data.model.*
import com.necrosed.noesis.data.repository.ConceptRepository
import com.necrosed.noesis.data.repository.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════
// NOESIS — MAIN VIEW MODEL
//
// The home screen evolves as the archive matures:
//   < 10 entries  → pure capture terminal
//   10–30 entries → capture + recent stream preview
//   > 30 entries  → capture + stream preview + pattern panel
//
// The archive does not nag. The analysis surface earns its place.
// ═══════════════════════════════════════════════════════════════

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db: NoesisDatabase by lazy {
        NoesisDatabase.getInstance(application)
    }
    private val entryRepo: EntryRepository by lazy {
        EntryRepository(db.entryDao(), db.conceptDao(), db.compositionDao())
    }
    private val conceptRepo: ConceptRepository by lazy {
        ConceptRepository(db.conceptDao(), db.entryDao())
    }

    // ─── LOADING ────────────────────────────────────────────────

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ─── CAPTURE STATE ──────────────────────────────────────────

    private val _captureText = MutableStateFlow("")
    val captureText: StateFlow<String> = _captureText.asStateFlow()

    private val _captureStatus = MutableStateFlow<CaptureStatus>(CaptureStatus.Idle)
    val captureStatus: StateFlow<CaptureStatus> = _captureStatus.asStateFlow()

    private val _compositionStatus = MutableStateFlow<CompositionStatus>(CompositionStatus.Unavailable)
    val compositionStatus: StateFlow<CompositionStatus> = _compositionStatus.asStateFlow()

    private val _modelStatus = MutableStateFlow<ModelStatus>(ModelStatus.Checking)
    val modelStatus: StateFlow<ModelStatus> = _modelStatus.asStateFlow()

    private val _selectedComposition = MutableStateFlow<Composition?>(null)
    val selectedComposition: StateFlow<Composition?> = _selectedComposition.asStateFlow()

    // ─── STREAM ─────────────────────────────────────────────────

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val entries: StateFlow<List<Entry>> = _searchQuery
        .debounce(200)
        .flatMapLatest { q ->
            if (q.isBlank()) entryRepo.observeStream()
            else entryRepo.searchStream(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recent entries for home screen preview (last 5)
    val recentEntries: StateFlow<List<Entry>> = entries
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── CONCEPTS ───────────────────────────────────────────────

    val concepts: StateFlow<List<Concept>> = conceptRepo
        .observeAllRanked()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val significantConcepts: StateFlow<List<Concept>> = conceptRepo
        .observeSignificant()
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ─── ARCHIVE STATS ──────────────────────────────────────────

    private val _window = MutableStateFlow(AnalysisWindow.DAYS_30)
    val window: StateFlow<AnalysisWindow> = _window.asStateFlow()

    private val _stats = MutableStateFlow<ArchiveStats?>(null)
    val stats: StateFlow<ArchiveStats?> = _stats.asStateFlow()

    // ─── ARCHIVE MATURITY ───────────────────────────────────────
    // Controls which panels the home screen shows

    val archiveMaturity: StateFlow<ArchiveMaturity> = entries
        .map { list ->
            when {
                list.size >= 30 -> ArchiveMaturity.MATURE
                list.size >= 10 -> ArchiveMaturity.DEVELOPING
                else            -> ArchiveMaturity.NASCENT
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ArchiveMaturity.NASCENT)

    // ─── CONCEPT DETAIL ─────────────────────────────────────────

    private val _selectedConcept = MutableStateFlow<Concept?>(null)
    val selectedConcept: StateFlow<Concept?> = _selectedConcept.asStateFlow()

    private val _selectedConceptEntries = MutableStateFlow<List<Entry>>(emptyList())
    val selectedConceptEntries: StateFlow<List<Entry>> = _selectedConceptEntries.asStateFlow()

    // ─── ENTRY DETAIL ───────────────────────────────────────────

    private val _selectedEntry = MutableStateFlow<Entry?>(null)
    val selectedEntry: StateFlow<Entry?> = _selectedEntry.asStateFlow()

    private val _selectedEntryRevisions = MutableStateFlow<List<Revision>>(emptyList())
    val selectedEntryRevisions: StateFlow<List<Revision>> = _selectedEntryRevisions.asStateFlow()

    // ─── INIT ───────────────────────────────────────────────────

    init {
        entryRepo.configureComposition(application)
        viewModelScope.launch(Dispatchers.IO) {
            val manager = entryRepo.compositionManager(application)
            val c = manager.compatibility()
            _modelStatus.value = when {
                !c.compatible -> ModelStatus.Incompatible(c.reason ?: "Device is not compatible")
                manager.isInstalled() -> ModelStatus.Ready
                else -> ModelStatus.NotInstalled(c.ramGb, c.freeStorageGb)
            }
        }
        viewModelScope.launch {
            // Give the DB a moment to decrypt and init
            kotlinx.coroutines.delay(300)
            _isLoading.value = false
        }
        observeStats()
    }

    private fun observeStats() {
        viewModelScope.launch {
            entries.collect { _ ->
                _stats.value = conceptRepo.getStats(_window.value)
            }
        }
    }

    // ─── CAPTURE ACTIONS ────────────────────────────────────────

    fun onCaptureTextChange(text: String) {
        _captureText.value = text
        _captureStatus.value = CaptureStatus.Idle
    }

    fun archiveThought() {
        val text = _captureText.value.trim()
        if (text.isBlank()) return
        _captureStatus.value = CaptureStatus.Archiving

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val number = entryRepo.captureEntry(CaptureInput(text))
                _captureText.value = ""
                _captureStatus.value = CaptureStatus.Archived(
                    entryId = "N-${number.toString().padStart(4, '0')}"
                )
                _stats.value = conceptRepo.getStats(_window.value)

                // Never make capture wait for NLP/LLM inference.
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        entryRepo.analyzeCapturedEntry(number)
                        _compositionStatus.value = CompositionStatus.Composing(number)
                        entryRepo.composeEntry(number)
                        _compositionStatus.value = CompositionStatus.Ready(number)
                    } catch (e: Throwable) {
                        _compositionStatus.value = CompositionStatus.Error(number, e.message ?: "Local composition failed")
                    }
                }
            } catch (e: Exception) {
                _captureStatus.value = CaptureStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearCaptureStatus() {
        _captureStatus.value = CaptureStatus.Idle
    }

    fun installLocalModel() {
        if (_modelStatus.value is ModelStatus.Downloading) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val manager = entryRepo.compositionManager(getApplication())
                val c = manager.compatibility()
                if (!c.compatible) {
                    _modelStatus.value = ModelStatus.Incompatible(c.reason ?: "Device is not compatible")
                    return@launch
                }
                _modelStatus.value = ModelStatus.Downloading(0)
                manager.download { progress -> _modelStatus.value = ModelStatus.Downloading(progress) }
                _modelStatus.value = ModelStatus.Ready
            } catch (e: Throwable) {
                _modelStatus.value = ModelStatus.Error(e.message ?: "Model download failed")
            }
        }
    }

    fun composeSelectedEntry(entryNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _compositionStatus.value = CompositionStatus.Composing(entryNumber)
                _selectedComposition.value = entryRepo.composeEntry(entryNumber)
                _compositionStatus.value = CompositionStatus.Ready(entryNumber)
            } catch (e: Throwable) {
                _compositionStatus.value = CompositionStatus.Error(entryNumber, e.message ?: "Local composition failed")
            }
        }
    }

    // ─── STREAM ACTIONS ─────────────────────────────────────────

    fun setSearchQuery(q: String) { _searchQuery.value = q }

    fun openEntry(entryNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedEntry.value = entryRepo.getEntry(entryNumber)
            _selectedEntryRevisions.value = entryRepo.getRevisions(entryNumber)
            _selectedComposition.value = entryRepo.getComposition(entryNumber)
        }
    }

    fun clearSelectedEntry() {
        _selectedEntry.value = null
        _selectedEntryRevisions.value = emptyList()
        _selectedComposition.value = null
    }

    fun toggleUnresolved(entryNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepo.toggleUnresolved(entryNumber)
            _selectedEntry.value = entryRepo.getEntry(entryNumber)
        }
    }

    fun reviseEntry(entryNumber: Int, newContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepo.reviseEntry(entryNumber, newContent)
            _selectedEntry.value = entryRepo.getEntry(entryNumber)
            _selectedEntryRevisions.value = entryRepo.getRevisions(entryNumber)
            _selectedComposition.value = entryRepo.getComposition(entryNumber)
        }
    }

    fun archiveEntry(entryNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepo.archiveEntry(entryNumber)
            clearSelectedEntry()
        }
    }

    fun purgeEntry(entryNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepo.purgeEntry(entryNumber)
            clearSelectedEntry()
        }
    }

    // ─── CONCEPT ACTIONS ────────────────────────────────────────

    fun openConcept(conceptNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedConcept.value = conceptRepo.getConcept(conceptNumber)
            _selectedConceptEntries.value =
                conceptRepo.getEntriesForConcept(conceptNumber)
        }
    }

    fun clearSelectedConcept() {
        _selectedConcept.value = null
        _selectedConceptEntries.value = emptyList()
    }

    // ─── WINDOW ─────────────────────────────────────────────────

    fun setWindow(w: AnalysisWindow) {
        _window.value = w
        viewModelScope.launch(Dispatchers.IO) {
            _stats.value = conceptRepo.getStats(w)
        }
    }
}

// ─── SUPPORTING TYPES ───────────────────────────────────────────

sealed class CaptureStatus {
    object Idle                           : CaptureStatus()
    object Archiving                      : CaptureStatus()
    data class Archived(val entryId: String) : CaptureStatus()
    data class Error(val message: String) : CaptureStatus()
}

enum class ArchiveMaturity {
    NASCENT,     // < 10 entries: capture terminal only
    DEVELOPING,  // 10–30 entries: capture + recent stream
    MATURE       // > 30 entries: capture + stream + pattern panel
}


sealed class CompositionStatus {
    object Unavailable : CompositionStatus()
    data class Composing(val entryNumber: Int) : CompositionStatus()
    data class Ready(val entryNumber: Int) : CompositionStatus()
    data class Error(val entryNumber: Int, val message: String) : CompositionStatus()
}


sealed class ModelStatus {
    object Checking : ModelStatus()
    data class NotInstalled(val ramGb: Long, val freeStorageGb: Long) : ModelStatus()
    data class Downloading(val progress: Int) : ModelStatus()
    object Ready : ModelStatus()
    data class Incompatible(val reason: String) : ModelStatus()
    data class Error(val message: String) : ModelStatus()
}
