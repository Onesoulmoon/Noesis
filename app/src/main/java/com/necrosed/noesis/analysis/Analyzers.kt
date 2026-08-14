package com.necrosed.noesis.analysis

// ═══════════════════════════════════════════════════════════════
// FRENCH STEMMER — Snowball French, simplified subset
// ═══════════════════════════════════════════════════════════════

class FrenchStemmer {

    fun stem(word: String): String {
        var w = word.lowercase().trim()
        if (w.length <= 2) return w
        w = removePlural(w)
        w = removeVerbSuffix(w)
        w = removeNounSuffix(w)
        w = removeAdjSuffix(w)
        return w
    }

    private fun removePlural(s: String): String = when {
        s.endsWith("eaux") -> s.dropLast(1)   // chevaux → cheval
        s.endsWith("aux")  -> s.dropLast(2) + "l"
        s.endsWith("x")    -> if (s.length > 4) s.dropLast(1) else s
        s.endsWith("s")    -> if (s.length > 4) s.dropLast(1) else s
        else -> s
    }

    private fun removeVerbSuffix(s: String): String {
        val verbSuffixes = listOf(
            "issement", "issions", "issais", "issait", "issaient",
            "iront", "iraient", "iriez", "irions", "issions",
            "aient", "asses", "erait", "erons", "eront",
            "ation", "ations", "ement", "ements",
            "aient", "erez", "eras",
            "ons", "ant", "ant", "ez", "er", "ir", "re"
        )
        for (suffix in verbSuffixes.sortedByDescending { it.length }) {
            if (s.endsWith(suffix) && s.length - suffix.length >= 3) {
                return s.dropLast(suffix.length)
            }
        }
        return s
    }

    private fun removeNounSuffix(s: String): String {
        val nounSuffixes = listOf(
            "ifications", "ification", "ifications",
            "isation", "isations", "isation",
            "isation", "ement", "ements",
            "iste", "istes", "isme", "ismes",
            "ité", "ités", "eur", "eurs",
            "tion", "tions", "sion", "sions"
        )
        for (suffix in nounSuffixes.sortedByDescending { it.length }) {
            if (s.endsWith(suffix) && s.length - suffix.length >= 3) {
                return s.dropLast(suffix.length)
            }
        }
        return s
    }

    private fun removeAdjSuffix(s: String): String {
        val adjSuffixes = listOf(
            "ienne", "ien", "elle", "el",
            "euse", "eux", "ive", "if",
            "able", "ible"
        )
        for (suffix in adjSuffixes.sortedByDescending { it.length }) {
            if (s.endsWith(suffix) && s.length - suffix.length >= 3) {
                return s.dropLast(suffix.length)
            }
        }
        return s
    }
}

// ═══════════════════════════════════════════════════════════════
// ENGLISH ANALYZER
// ═══════════════════════════════════════════════════════════════

class EnglishAnalyzer : LanguageAnalyzer {
    override val languageCode = "en"
    override val stopWords = EnglishStopWords.words
    private val stemmer = PorterStemmer()

    override fun tokenize(text: String): List<String> =
        text.split(Regex("[\\s\\p{Punct}—–]+"))
            .map { normalize(it) }
            .filter { it.length >= 2 }

    override fun normalize(token: String): String =
        token.lowercase()
            .replace(Regex("[^a-z0-9']"), "")
            .replace(Regex("'s$"), "")
            .replace(Regex("'"), "")
            .trim()

    override fun stem(word: String): String = stemmer.stem(word)

    override fun detectLanguage(text: String): Float {
        val tokens = text.lowercase().split(Regex("\\s+"))
        val englishHits = tokens.count { it in EnglishStopWords.words }
        return (englishHits.toFloat() / tokens.size.coerceAtLeast(1)).coerceIn(0f, 1f)
    }

    override fun analyze(text: String): TokenizedEntry {
        val raw = tokenize(text)
        val filtered = raw.filter { it !in stopWords && it.length >= 3 }
        val stemmedPairs = filtered.map { stem(it) to it }
        val termFreq = buildTermFrequency(stemmedPairs)
        val phrases = extractBigrams(filtered) + extractTrigrams(filtered)
        val scoredPhrases = scorePhrases(phrases)
        return TokenizedEntry(raw, filtered, stemmedPairs, termFreq, scoredPhrases)
    }
}

// ═══════════════════════════════════════════════════════════════
// FRENCH ANALYZER
// ═══════════════════════════════════════════════════════════════

class FrenchAnalyzer : LanguageAnalyzer {
    override val languageCode = "fr"
    override val stopWords = FrenchStopWords.words
    private val stemmer = FrenchStemmer()

    override fun tokenize(text: String): List<String> =
        text.split(Regex("[\\s\\p{Punct}—–]+"))
            .map { normalize(it) }
            .filter { it.length >= 2 }

    override fun normalize(token: String): String =
        token.lowercase()
            .replace(Regex("[^a-zàâäéèêëîïôöùûüç0-9]"), "")
            .trim()

    override fun stem(word: String): String = stemmer.stem(word)

    override fun detectLanguage(text: String): Float {
        val tokens = text.lowercase().split(Regex("\\s+"))
        val frenchHits = tokens.count { it in FrenchStopWords.words }
        return (frenchHits.toFloat() / tokens.size.coerceAtLeast(1)).coerceIn(0f, 1f)
    }

    override fun analyze(text: String): TokenizedEntry {
        val raw = tokenize(text)
        val filtered = raw.filter { it !in stopWords && it.length >= 3 }
        val stemmedPairs = filtered.map { stem(it) to it }
        val termFreq = buildTermFrequency(stemmedPairs)
        val phrases = extractBigrams(filtered) + extractTrigrams(filtered)
        val scoredPhrases = scorePhrases(phrases)
        return TokenizedEntry(raw, filtered, stemmedPairs, termFreq, scoredPhrases)
    }
}

// ═══════════════════════════════════════════════════════════════
// GENERIC ANALYZER — Fallback for unknown languages
// ═══════════════════════════════════════════════════════════════

class GenericAnalyzer : LanguageAnalyzer {
    override val languageCode = "generic"
    override val stopWords = EnglishStopWords.words + FrenchStopWords.words

    override fun tokenize(text: String): List<String> =
        text.split(Regex("[\\s\\p{Punct}—–]+"))
            .map { normalize(it) }
            .filter { it.length >= 2 }

    override fun normalize(token: String): String =
        token.lowercase().replace(Regex("[^\\w]"), "").trim()

    override fun stem(word: String): String = word.lowercase().trim()

    override fun detectLanguage(text: String): Float = 0.5f

    override fun analyze(text: String): TokenizedEntry {
        val raw = tokenize(text)
        val filtered = raw.filter { it !in stopWords && it.length >= 3 }
        val stemmedPairs = filtered.map { stem(it) to it }
        val termFreq = buildTermFrequency(stemmedPairs)
        val phrases = extractBigrams(filtered)
        val scoredPhrases = scorePhrases(phrases)
        return TokenizedEntry(raw, filtered, stemmedPairs, termFreq, scoredPhrases)
    }
}

// ═══════════════════════════════════════════════════════════════
// SHARED UTILITIES — used by all analyzer implementations
// ═══════════════════════════════════════════════════════════════

internal fun buildTermFrequency(
    stemmedPairs: List<Pair<String, String>>
): Map<String, TermInfo> {
    val map = mutableMapOf<String, TermInfo>()
    for ((stem, original) in stemmedPairs) {
        val existing = map[stem]
        map[stem] = if (existing == null) {
            TermInfo(stem, 1, setOf(original))
        } else {
            existing.copy(count = existing.count + 1, forms = existing.forms + original)
        }
    }
    return map
}

internal fun extractBigrams(tokens: List<String>): List<List<String>> {
    if (tokens.size < 2) return emptyList()
    return (0 until tokens.size - 1).map { listOf(tokens[it], tokens[it + 1]) }
}

internal fun extractTrigrams(tokens: List<String>): List<List<String>> {
    if (tokens.size < 3) return emptyList()
    return (0 until tokens.size - 2).map {
        listOf(tokens[it], tokens[it + 1], tokens[it + 2])
    }
}

internal fun scorePhrases(ngrams: List<List<String>>): List<PhraseCandidate> {
    val freq = mutableMapOf<String, MutableList<List<String>>>()
    for (ng in ngrams) {
        val key = ng.joinToString(" ")
        freq.getOrPut(key) { mutableListOf() }.add(ng)
    }
    return freq
        .filter { (_, v) -> v.size >= 2 }   // Must appear 2+ times to be a phrase
        .map { (phrase, occurrences) ->
            PhraseCandidate(
                phrase = phrase,
                stem   = phrase,
                count  = occurrences.size,
                tokens = occurrences.first()
            )
        }
        .sortedByDescending { it.count }
}

// ─── LANGUAGE DETECTION ─────────────────────────────────────────

fun detectLanguage(text: String): String {
    val enScore = EnglishAnalyzer().detectLanguage(text)
    val frScore = FrenchAnalyzer().detectLanguage(text)
    return when {
        enScore > 0.08f && enScore > frScore -> "en"
        frScore > 0.08f && frScore > enScore -> "fr"
        else -> "unknown"
    }
}

fun analyzerFor(language: String): LanguageAnalyzer = when (language) {
    "en" -> EnglishAnalyzer()
    "fr" -> FrenchAnalyzer()
    else -> GenericAnalyzer()
}
