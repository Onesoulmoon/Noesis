package com.necrosed.noesis.ai

enum class AiRole(
    val id: String,
    val displayName: String,
    val description: String,
    val iconName: String
) {
    SYNTHESIS(
        id = "synthesis",
        displayName = "Synthesis",
        description = "Distills chaotic notes into condensed, core insights.",
        iconName = "Compress"
    ),
    CORRECTION(
        id = "correction",
        displayName = "Clarity & Grammar",
        description = "Polishes prose and fixes grammar while maintaining tone.",
        iconName = "Edit"
    ),
    ANALYSIS(
        id = "analysis",
        displayName = "Deep Analysis",
        description = "Examines premises, logical coherence, and underlying themes.",
        iconName = "Psychology"
    ),
    RECOMMENDATION(
        id = "recommendation",
        displayName = "Improvements",
        description = "Identifies structural gaps and suggests actionable refinements.",
        iconName = "AutoAwesome"
    ),
    PROGRESS_TRACKER(
        id = "progress_tracker",
        displayName = "Progress Tracker",
        description = "Evaluates shifts in perspective and ongoing thought trends.",
        iconName = "Timeline"
    ),
    CONNECTOR(
        id = "connector",
        displayName = "Connections",
        description = "Maps relationships across entries and clusters ideas.",
        iconName = "Hub"
    ),
    SOCRATIC(
        id = "socratic",
        displayName = "Socratic Probe",
        description = "Generates targeted questions to deepen understanding.",
        iconName = "HelpOutline"
    ),
    ACTION_PLANNER(
        id = "action_planner",
        displayName = "Action Items",
        description = "Extracts tasks and actionable items into a clean checklist.",
        iconName = "CheckCircle"
    ),
    DEVILS_ADVOCATE(
        id = "devils_advocate",
        displayName = "Devil's Advocate",
        description = "Challenges arguments and presents counter-perspectives.",
        iconName = "Shield"
    ),
    COUNTER_PERSPECTIVE(
        id = "counter_perspective",
        displayName = "Devil's Advocate",
        description = "Challenges arguments and presents counter-perspectives.",
        iconName = "Shield"
    );

    companion object {
        fun fromId(id: String): AiRole =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: SYNTHESIS

        fun from(name: String?): AiRole =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.id.equals(name, ignoreCase = true) } ?: SYNTHESIS
    }
}

object RolePromptFactory {

    fun createPrompt(role: AiRole, inputContent: String, extraContext: String? = null): String {
        return buildPrompt(role = role, contextData = inputContent, promptSuffix = extraContext ?: "")
    }

    fun buildPrompt(role: AiRole, contextData: String, promptSuffix: String = ""): String {
        val roleDirective = when (role) {
            AiRole.SYNTHESIS -> """
                ROLE: Elite Knowledge Synthesizer.
                GOAL: Distill the input notes into condensed, core insights and key arguments.
                INSTRUCTIONS:
                - Create descriptive titles capturing the main points (e.g., "CORE THESIS", "SUPPORTING EVIDENCE").
                - DO NOT default to template headers like "Diagnosing Motivation" unless explicitly present in the input.
            """.trimIndent()

            AiRole.CORRECTION -> """
                ROLE: Copy Editor & Prose Refiner (Clarity & Grammar).
                GOAL: Rephrase, polish, and fix grammar, typos, punctuation, and flow while strictly preserving the author's authentic voice.
                INSTRUCTIONS:
                - DO NOT create motivational diagnosis headers, hypothesis sections, or meta-analysis.
                - Focus on returning clear, polished prose. Use section titles like "POLISHED TEXT" or "REFINED PROSE".
                - Put the primary polished text in the first section's content.
                - Use keyInsight to highlight the primary stylistic or grammatical improvement made.
            """.trimIndent()

            AiRole.ANALYSIS -> """
                ROLE: Analytical Philosopher & Cognitive Scientist (Deep Analysis).
                GOAL: Deconstruct the underlying premises, logical structures, implicit assumptions, and epistemic statuses.
                INSTRUCTIONS:
                - Examine core arguments, identify unspoken assumptions, and evaluate logical coherence.
                - Assign epistemic statuses (FACT, BELIEF, HYPOTHESIS, QUESTION, OBSERVATION) accurately to each section.
            """.trimIndent()

            AiRole.RECOMMENDATION -> """
                ROLE: Strategic Editor & Refinement Advisor.
                GOAL: Point out logic gaps, unaddressed counter-arguments, and recommend concrete, actionable improvements.
                INSTRUCTIONS:
                - Structure sections as specific improvements (e.g., "STRUCTURAL GAP", "RECOMMENDED REFLECTION").
            """.trimIndent()

            AiRole.PROGRESS_TRACKER -> """
                ROLE: Cognitive Progress Tracker.
                GOAL: Evaluate shifts in perspective and ongoing thought trends in the text.
                INSTRUCTIONS:
                - Identify evolving ideas, persistent convictions, and changing viewpoints.
            """.trimIndent()

            AiRole.CONNECTOR -> """
                ROLE: Knowledge Graph System.
                GOAL: Identify thematic nodes, linked concepts, and conceptual associations across the text.
                INSTRUCTIONS:
                - Group material by core themes and concepts.
            """.trimIndent()

            AiRole.SOCRATIC -> """
                ROLE: Socratic Dialogue Partner.
                GOAL: Challenge and deepen understanding through targeted questions.
                INSTRUCTIONS:
                - Formulate highly challenging, probing questions to test the author's assumptions.
                - Populate openQuestions with 3-5 sharp, probing questions.
            """.trimIndent()

            AiRole.ACTION_PLANNER -> """
                ROLE: Executive Productivity Engine.
                GOAL: Extract actionable tasks and next steps.
                INSTRUCTIONS:
                - Convert implicit or explicit to-dos into a structured checklist in the sections.
            """.trimIndent()

            AiRole.DEVILS_ADVOCATE, AiRole.COUNTER_PERSPECTIVE -> """
                ROLE: Critical Thinker & Devil's Advocate.
                GOAL: Challenge assumptions, point out flaws, blind spots, and counter-arguments directly.
                INSTRUCTIONS:
                - DO NOT format this as a standard synthesis note or motivational diagnosis.
                - Actively argue against the premises. Use section titles like "CHALLENGED ASSUMPTION", "COUNTER-ARGUMENT", "BLIND SPOT".
            """.trimIndent()
        }

        val schemaSpec = """
            Return ONLY a single valid JSON object matching EXACTLY this structure:
            {
              "title": "A concise title specific to this content and role",
              "subtitle": null,
              "sections": [
                {
                  "type": "ARGUMENT|OBSERVATION|QUESTION|TENSION|INTERPRETATION|CONTEXT|CONCLUSION",
                  "title": "Section Title Specific To Role and Content",
                  "content": "Detailed text content for this section",
                  "interpretation": "Brief rationale or null",
                  "epistemicStatus": "FACT|BELIEF|HYPOTHESIS|QUESTION|OBSERVATION",
                  "sourceFragments": ["exact short quotes from input"]
                }
              ],
              "keyInsight": "One line summary insight or null",
              "openQuestions": ["Question 1", "Question 2"]
            }
        """.trimIndent()

        val modifier = if (promptSuffix.isNotBlank()) "\nPROMPT MODIFIER: $promptSuffix\n" else ""

        return """
            <start_of_turn>user
            $roleDirective
            $modifier
            INPUT TEXT:
            \"\"\"
            $contextData
            \"\"\"

            CRITICAL RULES:
            1. Adapt your analysis strictly to the requested ROLE (${role.displayName}).
            2. DO NOT output conversational text, markdown pre-ambles, or ```json backticks.
            3. Return ONLY valid, parseable JSON matching the schema below.

            SCHEMA:
            $schemaSpec
            <end_of_turn>
            <start_of_turn>model
        """.trimIndent()
    }
}
