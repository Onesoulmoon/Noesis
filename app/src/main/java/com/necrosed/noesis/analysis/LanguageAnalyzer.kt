package com.necrosed.noesis.analysis

// ═══════════════════════════════════════════════════════════════
// NOESIS — LANGUAGE ANALYZER INTERFACE
//
// The analysis engine is language-aware but not language-locked.
// New languages can be added without touching the pipeline.
//
// v1 ships: EnglishAnalyzer, FrenchAnalyzer, GenericAnalyzer
// ═══════════════════════════════════════════════════════════════

data class TokenizedEntry(
    val rawTokens: List<String>,        // After tokenization + normalization
    val filteredTokens: List<String>,   // After stop word removal
    val stemmedPairs: List<Pair<String, String>>,  // (stem, original)
    val termFrequency: Map<String, TermInfo>,
    val phrasesCandidates: List<PhraseCandidate>
)

data class TermInfo(
    val stem: String,
    val count: Int,
    val forms: Set<String>             // Surface forms seen: {"build","building","built"}
)

data class PhraseCandidate(
    val phrase: String,                // "software build"
    val stem: String,                  // Normalized: "softwar build"
    val count: Int,
    val tokens: List<String>
)

interface LanguageAnalyzer {
    val languageCode: String
    val stopWords: Set<String>

    fun tokenize(text: String): List<String>
    fun normalize(token: String): String
    fun stem(word: String): String
    fun analyze(text: String): TokenizedEntry
    fun detectLanguage(text: String): Float  // 0.0–1.0 confidence for this language
}

// ─── ENGLISH STOP WORDS ─────────────────────────────────────────

object EnglishStopWords {
    val words = setOf(
        "a", "an", "the", "and", "or", "but", "nor", "so", "yet",
        "in", "on", "at", "to", "for", "of", "with", "by", "from",
        "up", "about", "into", "through", "during", "before", "after",
        "above", "below", "between", "out", "off", "over", "under",
        "is", "are", "was", "were", "be", "been", "being",
        "have", "has", "had", "do", "does", "did",
        "will", "would", "could", "should", "may", "might", "shall", "can",
        "i", "me", "my", "myself", "we", "our", "ours", "ourselves",
        "you", "your", "yours", "yourself",
        "he", "him", "his", "himself",
        "she", "her", "hers", "herself",
        "it", "its", "itself",
        "they", "them", "their", "theirs", "themselves",
        "what", "which", "who", "whom", "whose",
        "this", "that", "these", "those",
        "am", "not", "no", "nor",
        "as", "if", "then", "than", "when", "where", "while",
        "because", "though", "although", "since", "until", "unless",
        "also", "just", "only", "even", "still", "again", "too", "very",
        "more", "most", "much", "many", "some", "any", "each", "every",
        "here", "there", "now", "then", "always", "never", "sometimes",
        "dont", "wont", "cant", "im", "ive", "its", "thats", "its",
        "something", "anything", "everything", "nothing",
        "get", "got", "go", "going", "make", "making", "made",
        "think", "thinking", "thought", "know", "knowing", "knew",
        "want", "wanted", "need", "needed", "feel", "felt",
        "one", "two", "first", "second", "like", "well", "back", "way",
        "time", "day", "year", "work", "thing", "things", "kind"
    )
}

// ─── FRENCH STOP WORDS ──────────────────────────────────────────

object FrenchStopWords {
    val words = setOf(
        "le", "la", "les", "l", "un", "une", "des", "du", "de",
        "et", "ou", "mais", "donc", "or", "ni", "car",
        "que", "qui", "quoi", "dont", "où", "quand", "comment", "pourquoi",
        "je", "me", "mon", "ma", "mes", "moi",
        "tu", "te", "ton", "ta", "tes", "toi",
        "il", "lui", "son", "sa", "ses", "lui",
        "elle", "elle",
        "nous", "notre", "nos",
        "vous", "votre", "vos",
        "ils", "elles", "leur", "leurs",
        "ce", "cet", "cette", "ces", "ceci", "cela", "ça",
        "se", "si", "ne", "pas", "plus", "rien", "jamais",
        "très", "bien", "aussi", "comme", "encore", "même",
        "en", "au", "aux", "sur", "sous", "dans", "avec", "sans",
        "par", "pour", "contre", "entre", "vers", "chez",
        "est", "sont", "était", "étaient", "être", "avoir", "avait",
        "suis", "es", "a", "avons", "avez", "ont",
        "faire", "fait", "fais", "penser", "pense", "vouloir",
        "tout", "tous", "toute", "toutes", "autre", "autres",
        "on", "y", "en", "dont", "puis", "car", "donc",
        "savoir", "sais", "sait", "pouvoir", "peut", "peuvent",
        "vais", "aller", "allons", "allez", "vont",
        "quand", "alors", "maintenant", "toujours", "souvent",
        "quelque", "quelques", "certain", "certains", "plusieurs"
    )
}
