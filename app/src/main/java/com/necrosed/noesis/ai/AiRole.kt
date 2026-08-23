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
        val systemInstruction = when (role) {
            AiRole.SYNTHESIS ->
                "You are an elite knowledge synthesizer. Summarize the text accurately without losing critical concepts."

            AiRole.CORRECTION ->
                "You are a meticulous prose editor. Fix grammar, syntax, and phrasing while preserving the author's original voice. Output clear revised text and explanations."

            AiRole.ANALYSIS ->
                "You are an analytical philosopher and cognitive scientist. Deconstruct the underlying premises, logical structures, and implicit assumptions."

            AiRole.RECOMMENDATION ->
                "You are a strategic editor. Point out logic gaps, unaddressed counter-arguments, and recommend 3 direct improvements."

            AiRole.PROGRESS_TRACKER ->
                "You are a cognitive progress tracker. Analyze these chronological notes and output a summary of how the author's ideas have evolved."

            AiRole.CONNECTOR ->
                "You are a graph database system. Identify thematic nodes, linked concepts, and conceptual associations across the text."

            AiRole.SOCRATIC ->
                "You are a Socratic dialogue partner. Do not edit or summarize. Ask 3 concise, highly challenging questions that encourage deeper reflection."

            AiRole.ACTION_PLANNER ->
                "You are an executive productivity engine. Extract all actionable tasks, next steps, or implicit to-dos into a clean Markdown checklist."

            AiRole.DEVILS_ADVOCATE, AiRole.COUNTER_PERSPECTIVE ->
                "You are a rigorous debate advisor. Identify weaknesses in the argument and formulate valid counter-perspectives to test its strength."
        }

        val rules = """
            RULES:
            - Distinguish observations (the author's ideas) from interpretations (your analysis).
            - Use precise, descriptive titles for sections.
            - For each section, provide a list of "sourceFragments" (exact short quotes from the input) that justify the section.
            - For each section, provide an "interpretation" field explaining WHY these fragments belong together in this section.
            - For each section, assess the "epistemicStatus": FACT, BELIEF, HYPOTHESIS, QUESTION, or OBSERVATION.
            - Make the result substantially more concise and structured than the raw input.
            - Return ONLY valid JSON matching this schema:
            {"title":string,"subtitle":string|null,"sections":[{"type":"ARGUMENT|OBSERVATION|QUESTION|TENSION|INTERPRETATION|CONTEXT|CONCLUSION","title":string,"content":string,"interpretation":string|null,"epistemicStatus":"FACT|BELIEF|HYPOTHESIS|QUESTION|OBSERVATION"|null,"sourceFragments":[string]}],"keyInsight":string|null,"openQuestions":[string]}
        """.trimIndent()

        val contextBlock = if (promptSuffix.isNotBlank()) "\nAdditional Historical Context:\n\"\"\"\n$promptSuffix\n\"\"\"\n" else ""

        return """
            <start_of_turn>user
            Role Instruction: $systemInstruction
            $contextBlock
            Target Input:
            \"\"\"
            $contextData
            \"\"\"
            $rules
            <end_of_turn>
            <start_of_turn>model
        """.trimIndent()
    }
}
