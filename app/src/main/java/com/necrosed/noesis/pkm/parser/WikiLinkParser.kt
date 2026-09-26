package com.necrosed.noesis.pkm.parser

import java.util.regex.Pattern

data class ParsedEntry(
    val rawContent: String,
    val extractedConcepts: List<String>
)

object WikiLinkParser {
    // Regex matching [[Concept Title]]
    private val WIKI_LINK_REGEX: Pattern = Pattern.compile("\\[\\[(.*?)]]")

    fun parse(content: String): ParsedEntry {
        val matcher = WIKI_LINK_REGEX.matcher(content)
        val concepts = mutableListOf<String>()

        while (matcher.find()) {
            matcher.group(1)?.trim()?.let { concept ->
                if (concept.isNotEmpty()) {
                    concepts.add(concept)
                }
            }
        }

        return ParsedEntry(
            rawContent = content,
            extractedConcepts = concepts.distinct()
        )
    }
}
