package com.necrosed.noesis.analysis

// ═══════════════════════════════════════════════════════════════
// PORTER STEMMER — Martin Porter, 1980
//
// Reduces English words to their root morphological form.
// "building" → "build", "philosophy" → "philosophi"
//
// This is deterministic and inspectable — the user can always
// see exactly why two words were grouped together.
// ═══════════════════════════════════════════════════════════════

class PorterStemmer {

    fun stem(word: String): String {
        var w = word.lowercase().trim()
        if (w.length <= 2) return w
        w = step1a(w)
        w = step1b(w)
        w = step1c(w)
        w = step2(w)
        w = step3(w)
        w = step4(w)
        w = step5a(w)
        w = step5b(w)
        return w
    }

    // ─── VOWEL / MEASURE UTILITIES ──────────────────────────────

    private fun isVowel(c: Char) = c in "aeiou"

    private fun isVowelAt(s: String, i: Int): Boolean {
        if (i < 0 || i >= s.length) return false
        return if (s[i] == 'y') i > 0 && !isVowel(s[i - 1])
        else isVowel(s[i])
    }

    // m = number of VC sequences in s
    private fun measure(s: String): Int {
        var m = 0
        var inVowel = false
        for (i in s.indices) {
            if (isVowelAt(s, i)) {
                inVowel = true
            } else if (inVowel) {
                m++
                inVowel = false
            }
        }
        return m
    }

    private fun containsVowel(s: String) = s.indices.any { isVowelAt(s, it) }

    private fun endsDoubleConsonant(s: String): Boolean {
        if (s.length < 2) return false
        val last = s[s.length - 1]
        return !isVowel(last) && s[s.length - 2] == last
    }

    // Ends with CVC and the final consonant is not w, x, or y
    private fun endsCVC(s: String): Boolean {
        if (s.length < 3) return false
        val c1 = !isVowelAt(s, s.length - 3)
        val v  =  isVowelAt(s, s.length - 2)
        val c2 = !isVowelAt(s, s.length - 1)
        val notWXY = s[s.length - 1] !in "wxy"
        return c1 && v && c2 && notWXY
    }

    // ─── STEP 1A ────────────────────────────────────────────────
    // sses → ss   |  ies → i   |  ss → ss   |  s → ""

    private fun step1a(s: String): String = when {
        s.endsWith("sses") -> s.dropLast(2)
        s.endsWith("ies")  -> s.dropLast(2) + "i"
        s.endsWith("ss")   -> s
        s.endsWith("s")    -> s.dropLast(1)
        else -> s
    }

    // ─── STEP 1B ────────────────────────────────────────────────
    // (m>0) eed → ee   |  (*v*) ed → ""   |  (*v*) ing → ""

    private fun step1b(s: String): String {
        if (s.endsWith("eed")) {
            val stem = s.dropLast(3)
            return if (measure(stem) > 0) stem + "ee" else s
        }
        val (trimmed, matched) = when {
            s.endsWith("ing") -> Pair(s.dropLast(3), true)
            s.endsWith("ed")  -> Pair(s.dropLast(2), true)
            else -> Pair(s, false)
        }
        if (!matched || !containsVowel(trimmed)) return s
        return when {
            trimmed.endsWith("at") || trimmed.endsWith("bl") || trimmed.endsWith("iz") ->
                trimmed + "e"
            endsDoubleConsonant(trimmed) &&
                trimmed.last() !in "lsz" -> trimmed.dropLast(1)
            measure(trimmed) == 1 && endsCVC(trimmed) ->
                trimmed + "e"
            else -> trimmed
        }
    }

    // ─── STEP 1C ────────────────────────────────────────────────
    // (*v*) y → i

    private fun step1c(s: String): String {
        if (s.endsWith("y") && containsVowel(s.dropLast(1))) {
            return s.dropLast(1) + "i"
        }
        return s
    }

    // ─── STEP 2 ─────────────────────────────────────────────────
    // Longer double-suffix removal (m>0)

    private val step2Rules = listOf(
        "ational" to "ate",  "tional" to "tion",  "enci"  to "ence",
        "anci"    to "ance", "izer"   to "ize",   "abli"  to "able",
        "alli"    to "al",   "entli"  to "ent",   "eli"   to "e",
        "ousli"   to "ous",  "ization" to "ize",   "ation" to "ate",
        "ator"    to "ate",  "alism"  to "al",    "iveness" to "ive",
        "fulness" to "ful",  "ousness" to "ous",   "aliti" to "al",
        "iviti"   to "ive",  "biliti" to "ble"
    )

    private fun step2(s: String): String {
        for ((suffix, replace) in step2Rules) {
            if (s.endsWith(suffix)) {
                val stem = s.dropLast(suffix.length)
                if (measure(stem) > 0) return stem + replace
            }
        }
        return s
    }

    // ─── STEP 3 ─────────────────────────────────────────────────

    private val step3Rules = listOf(
        "icate" to "ic", "ative" to "", "alize" to "al",
        "iciti" to "ic", "ical"  to "ic", "ful" to "", "ness" to ""
    )

    private fun step3(s: String): String {
        for ((suffix, replace) in step3Rules) {
            if (s.endsWith(suffix)) {
                val stem = s.dropLast(suffix.length)
                if (measure(stem) > 0) return stem + replace
            }
        }
        return s
    }

    // ─── STEP 4 ─────────────────────────────────────────────────

    private val step4Suffixes = listOf(
        "al", "ance", "ence", "er", "ic", "able", "ible", "ant",
        "ement", "ment", "ent", "ion", "ou", "ism", "ate", "iti",
        "ous", "ive", "ize"
    )

    private fun step4(s: String): String {
        for (suffix in step4Suffixes) {
            if (s.endsWith(suffix)) {
                val stem = s.dropLast(suffix.length)
                if (suffix == "ion") {
                    if (measure(stem) > 1 && (stem.endsWith("s") || stem.endsWith("t")))
                        return stem
                } else {
                    if (measure(stem) > 1) return stem
                }
            }
        }
        return s
    }

    // ─── STEP 5A ────────────────────────────────────────────────

    private fun step5a(s: String): String {
        if (s.endsWith("e")) {
            val stem = s.dropLast(1)
            val m = measure(stem)
            if (m > 1) return stem
            if (m == 1 && !endsCVC(stem)) return stem
        }
        return s
    }

    // ─── STEP 5B ────────────────────────────────────────────────

    private fun step5b(s: String): String {
        if (measure(s) > 1 && endsDoubleConsonant(s) && s.endsWith("l"))
            return s.dropLast(1)
        return s
    }
}
