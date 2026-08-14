# NOESIS

### *An encrypted local archive for thoughts, ideas, questions, observations, and the patterns that survive them.*

> **Record the thought. Preserve the observation. Discover what persists.**

NOESIS is a local-first cognitive archive designed to capture unfiltered thoughts and examine how ideas evolve over time.

It is not intended to be another generic notes application.

It is not a productivity manager.

It is not a mood tracker.

It is not an AI chatbot disguised as a journal.

NOESIS explores a different question:

> **What remains in your thinking after the individual thought has passed?**

A user records ordinary observations, questions, fragments, ideas, doubts, projects, memories, and passing thoughts.

NOESIS preserves those records exactly as they were written.

Then, separately, it analyzes the archive for recurring concepts, persistent ideas, relationships, unresolved questions, and patterns that emerge across time.

The distinction between the two is fundamental:

> **The user's thought is the source of truth.**
> **NOESIS's interpretation is only an interpretation.**

---

# TABLE OF CONTENTS

* [Overview](#overview)
* [The Problem](#the-problem)
* [What NOESIS Is](#what-noesis-is)
* [What NOESIS Is Not](#what-noesis-is-not)
* [Core Philosophy](#core-philosophy)
* [The Name](#the-name)
* [The Cognitive Archive Model](#the-cognitive-archive-model)
* [Capture](#capture)
* [Voice Input](#voice-input)
* [Records](#records)
* [Revisions](#revisions)
* [Permanent Entry Numbers](#permanent-entry-numbers)
* [Concept Detection](#concept-detection)
* [Transparent Analysis](#transparent-analysis)
* [Persistence Model](#persistence-model)
* [Thoughts vs Ideas](#thoughts-vs-ideas)
* [Unresolved Questions](#unresolved-questions)
* [Analysis Windows](#analysis-windows)
* [Concept Relationships](#concept-relationships)
* [Future Constellation Map](#future-constellation-map)
* [Notifications](#notifications)
* [Home Screen Widget](#home-screen-widget)
* [Privacy Model](#privacy-model)
* [Encryption](#encryption)
* [Security Philosophy](#security-philosophy)
* [Local-First Architecture](#local-first-architecture)
* [Data Model](#data-model)
* [Analysis Pipeline](#analysis-pipeline)
* [Technology Stack](#technology-stack)
* [Interface and Design System](#interface-and-design-system)
* [Typography](#typography)
* [Color System](#color-system)
* [Example Archive](#example-archive)
* [Installation](#installation)
* [Development](#development)
* [Building](#building)
* [Storage and Data](#storage-and-data)
* [Limitations](#limitations)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [Philosophy](#philosophy)
* [Project Status](#project-status)
* [Author](#author)

---

# Overview

Most digital journaling systems treat every entry as an isolated object.

You write something.

It gets a timestamp.

Maybe you add a tag.

Later you search for it.

NOESIS approaches the problem differently.

The archive is treated as a **temporal cognitive record**.

A single thought has little significance by itself.

A thought repeated over weeks may indicate an idea.

An idea that repeatedly changes form may indicate a deeper concern.

A question that remains unresolved for months may be more important than a hundred completed tasks.

NOESIS therefore focuses on:

```text id="jgujcl"
OBSERVATION
     ↓
REPETITION
     ↓
RELATION
     ↓
PERSISTENCE
     ↓
INTERPRETATION
```

The software does not decide what the user should think.

It attempts to reveal **what the user's archive already contains**.

---

# The Problem

Traditional note-taking systems are optimized for retrieval.

You search:

> “What did I write about cameras?”

and the application finds matching notes.

That is useful.

But it leaves a larger question unanswered:

> **What am I repeatedly thinking about without explicitly organizing it?**

People rarely record their thoughts in consistent categories.

A person may write:

```text id="xyf80v"
“I want to build something weird with Android.”
```

Two weeks later:

```text id="vo4x5u"
“I keep thinking about system-level software.”
```

Then:

```text id="1vbdpn"
“Maybe I should make tools that feel like instruments.”
```

The three entries may never share an explicit tag.

But something connects them.

NOESIS is designed to detect those connections without rewriting the user's original words.

---

# What NOESIS Is

NOESIS is:

* a private thought archive
* a temporal journal
* a concept detection system
* a persistence tracker
* an idea archaeology tool
* an unresolved-question repository
* a local-first cognitive database
* an experimental personal knowledge system

At its simplest:

```text id="baf7g9"
WRITE
  ↓
ARCHIVE
  ↓
OBSERVE
  ↓
ANALYZE
  ↓
DISCOVER
```

---

# What NOESIS Is Not

NOESIS deliberately avoids becoming:

### A conventional notes application

The point is not merely storing text.

### A productivity manager

Tasks can exist as thoughts, but task completion is not the central model.

### A mood tracker

NOESIS can contain emotional observations, but it does not reduce the user to metrics.

### A therapy application

The software does not diagnose, treat, or clinically interpret users.

### A social platform

Thoughts remain private.

### An AI chatbot

An AI layer may eventually become optional, but the archive should remain useful without one.

---

# Core Philosophy

NOESIS is built around several principles.

## 01 — Capture before classification

When a thought arrives, the user should not need to decide:

> Is this an idea?

> Is this a journal entry?

> Is this a task?

> Is this philosophy?

The user simply records it.

Classification happens later.

This reduces friction and prevents the user from forcing a thought into a category before understanding it.

---

## 02 — Preserve the original

NOESIS should never replace the user's original thought with an automatically summarized or “cleaned up” interpretation.

If the user writes:

```text id="qdewlz"
“I don't know why but I keep thinking
about abandoned projects.”
```

that exact observation remains in the archive.

A later concept such as:

> DIGITAL ABANDONMENT

exists separately.

---

## 03 — Interpretation is not truth

If NOESIS identifies:

```text id="1g35c5"
PERSISTENT CONCEPT

CREATION
```

that does not mean:

> “You are fundamentally obsessed with creation.”

It means:

> “The archive contains enough related observations for this concept to meet the current persistence criteria.”

That distinction is fundamental.

---

## 04 — Unknown is acceptable

Not every thought needs to become a concept.

Not every concept needs to become an insight.

Not every correlation is meaningful.

The system should be capable of saying:

```text id="11njv5"
INSUFFICIENT EVIDENCE
```

rather than inventing significance.

---

## 05 — Local-first by default

Thoughts are among the most private forms of user data.

NOESIS should therefore be designed so that its core functionality works without an account or remote backend.

---

# The Name

**Noesis** comes from the philosophical concept of direct intellectual apprehension or the act of thinking/understanding.

The name was chosen because NOESIS is not intended to simply collect text.

It is intended to examine the **process and persistence of thought**.

Within the larger fictional *Institutum Null* ecosystem, NOESIS represents the:

> **Cognitive Archive Division**

---

# The Cognitive Archive Model

NOESIS separates information into distinct layers.

```text id="g9y9tm"
RAW OBSERVATION
       │
       ↓
   RECORD / ENTRY
       │
       ↓
  TERMS / PHRASES
       │
       ↓
 CONCEPT CANDIDATE
       │
       ↓
 PERSISTENCE ANALYSIS
       │
       ↓
   IDEA / THREAD
```

The important part is that the lower-level observation is never discarded merely because a higher-level interpretation exists.

---

# Capture

Capture is the primary interaction.

NOESIS is designed around a **capture-first interface** rather than opening into a giant analytics dashboard.

A typical interaction:

```text id="2mav5a"
NOESIS
COGNITIVE ARCHIVE

────────────────────────────

What is on your mind?

> __________________________________
> __________________________________
> __________________________________

[ ARCHIVE ]

────────────────────────────

RECENT RECORDS

N-0087
N-0086
N-0085
```

The user should be able to record something in seconds.

There is no requirement to choose tags, categories, moods, or projects before saving.

---

# Voice Input

NOESIS can optionally support voice-to-text capture.

The flow should remain:

```text id="mf2w9z"
SPEAK
  ↓
TRANSCRIBE
  ↓
REVIEW
  ↓
ARCHIVE
```

NOESIS should not silently save an uncertain transcription.

The user gets the opportunity to inspect the text before it becomes part of the archive.

Voice input is optional.

Text remains the primary archival format.

---

# Records

Every thought becomes a permanent archive record.

A record can contain:

* text
* timestamp
* language
* creation metadata
* revision information
* derived terms
* concept relationships
* local analysis metadata

Example:

```text id="t8v4ol"
N-0084

“I keep coming back to the idea
of building software that feels
more like an instrument.”

12·08·2026
18:41
```

The record remains the original observation.

---

# Permanent Entry Numbers

NOESIS uses a global sequential numbering system.

```text id="tj5hqa"
N-0001
N-0002
N-0003
...
N-0147
```

Numbers never reset.

Numbers are never reused.

If a record is permanently purged, its identifier remains retired.

This preserves the integrity of the archive's chronology.

A future administrative view may show:

```text id="a4cl4t"
N-0142
STATUS: PURGED

Original content unavailable.
Identifier retained for archival continuity.
```

The numbering system should never depend on:

```text id="pr12go"
COUNT(entries) + 1
```

because deletion and synchronization could cause collisions.

A persistent sequence generator is used instead.

---

# Revisions

NOESIS allows entries to be edited, but edits do not silently overwrite history.

Instead, an edit produces a new revision.

Example:

```text id="g0ub4d"
N-0048

REVISION 01
“I think I want to build a
system for recurring thoughts.”

↓

REVISION 02
“I think the interesting part isn't
tracking thoughts. It's tracking
what survives.”
```

The current record can be displayed normally.

Historical revisions remain available for inspection.

This makes NOESIS partially append-oriented while remaining practical.

---

# Concept Detection

The first generation of NOESIS intentionally avoids opaque AI-driven interpretation.

Concept detection is designed to be:

* deterministic
* local
* inspectable
* reproducible
* lightweight

The initial analysis pipeline can be represented as:

```text id="ngui2s"
TOKENIZATION
      ↓
NORMALIZATION
      ↓
STOP-WORD FILTER
      ↓
STEMMING
      ↓
TERM FREQUENCY
      ↓
N-GRAM / PHRASE DETECTION
      ↓
CONCEPT CANDIDATES
      ↓
PERSISTENCE ANALYSIS
```

The goal is not to produce a perfect semantic understanding of the user.

The goal is to produce **useful, explainable signals**.

---

# Transparent Analysis

When NOESIS identifies a recurring concept, the user should be able to inspect the reasoning.

Example:

```text id="vo6a8i"
PERSISTENT CONCEPT

BUILDING SOFTWARE

CONFIDENCE
82%

OBSERVATIONS
N-0041
N-0062
N-0071
N-0084

TIME SPAN
19 DAYS

RECENT OCCURRENCES
3 / 14 DAYS

MATCH BASIS

“build”
“building”
“built”

normalized → build

PHRASE RELATION
“building software”

STATUS
PERSISTENT
```

The system does not need to say:

> “Your subconscious is telling you…”

It simply shows the archive's measurable properties.

---

# Persistence Model

NOESIS uses persistence rather than simple frequency.

A concept appearing ten times in one evening is not necessarily more persistent than something appearing four times across three weeks.

The initial model distinguishes:

## RECURRING

At least two related observations.

```text id="0nchth"
2+ observations
```

---

## PERSISTENT

A concept observed repeatedly across time.

Default criteria:

```text id="9jpdgp"
3+ observations
7+ days between first and latest observation
2+ observations within the last 14 days
```

---

## DEEPLY PERSISTENT

A concept that continues to appear over a substantially longer period.

Default criteria:

```text id="fipm96"
5+ observations
30+ day span
```

These thresholds can eventually become configurable.

---

# Thoughts vs Ideas

One of the most important distinctions in NOESIS is the separation between **thoughts** and **ideas**.

A thought is explicitly recorded by the user.

An idea is an inferred object derived from multiple thoughts.

Example:

```text id="i74d3b"
N-0042

“I should build a music player.”

↓

N-0068

“I keep thinking about that terminal
music player idea.”

↓

N-0081

“Maybe SYSCPU could have a music mode.”

↓

────────────────────────────

PERSISTENT IDEA

I-0012

TERMINAL MUSIC INTERFACE

3 observations
18 day span
82% persistence
```

The idea is not another note.

It is a **relationship between observations**.

---

# Unresolved Questions

NOESIS can also distinguish open questions from ordinary observations.

A question may become significant because it remains unresolved.

Example:

```text id="r5jww2"
QUESTION // Q-0017

“How does Android actually determine
thermal throttling?”

STATUS
UNRESOLVED

FIRST OBSERVED
02·08·2026

LAST OBSERVED
13·08·2026

RELATED CONCEPTS

ANDROID
THERMAL
SYSCPU
HARDWARE
```

A question can eventually transition through:

```text id="3h8e72"
UNKNOWN
    ↓
QUESTION
    ↓
INVESTIGATING
    ↓
HYPOTHESIS
    ↓
PARTIALLY VERIFIED
    ↓
RESOLVED
```

The system does not assume that every question requires an answer.

Some questions remain valuable precisely because they remain open.

---

# Analysis Windows

The archive is permanent.

Analysis is temporal.

NOESIS should support:

```text id="zqh5uj"
7 DAYS
30 DAYS
90 DAYS
1 YEAR
ALL TIME
CUSTOM
```

The same concept may therefore appear as:

```text id="ykmufk"
7 DAYS
DORMANT

30 DAYS
PERSISTENT

1 YEAR
DEEPLY PERSISTENT
```

This distinction prevents the analysis from collapsing the entire archive into one static ranking.

---

# Concept Relationships

A concept may be related to other concepts.

Example:

```text id="4m3lpr"
CREATION
   │
   ├──── SOFTWARE
   │
   ├──── IDENTITY
   │
   └──── LEARNING
```

Relationships may be based on:

* co-occurrence
* repeated temporal proximity
* phrase associations
* shared records
* user-confirmed relationships

These relationships form the foundation of the future cognitive constellation interface.

---

# Future Constellation Map

The constellation map is intentionally **not a first-version requirement**.

The underlying data model is designed so that it can eventually become a visual graph.

Conceptually:

```text id="d1a5i8"
                    SOFTWARE
                      ╱   ╲
                    ╱       ╲
              CREATION ─── LEARNING
                 │             │
                 │             │
             IDENTITY ────────┘
```

A future version can render the archive as a dynamic constellation.

Nodes represent concepts.

Edges represent relationships.

Size can represent persistence.

Brightness can represent recent activity.

Distance can represent conceptual similarity.

But visualization should come after the underlying cognitive model is stable.

---

# Notifications

NOESIS treats notifications as optional and deliberately sparse.

The application should not become a source of constant psychological interruption.

Potential notifications include:

### Persistent Idea

```text id="gn8em3"
NOESIS // OBSERVATION

A recurring concept has persisted
for 19 days.

[ VIEW ]
```

### Resurfacing Thought

```text id="ri7vde"
NOESIS // ARCHIVE

A thought last observed 47 days ago
has resurfaced.

[ VIEW ]
```

### Weekly Synthesis

```text id="6f6f0s"
NOESIS // WEEKLY RECORD

3 concepts persisted.
2 ideas resurfaced.
7 questions remain unresolved.
```

All notifications should be opt-in.

---

# Home Screen Widget

The first widget is intentionally simple.

```text id="5rf32o"
┌─────────────────────────────┐
│ NOESIS                      │
│ COGNITIVE ARCHIVE           │
│                             │
│ ACTIVE       047            │
│ PERSISTENT   008            │
│ UNRESOLVED   019            │
│                             │
│ [ + CAPTURE ]               │
└─────────────────────────────┘
```

The widget's purpose is to reduce capture friction.

A future **CURRENT THREAD** widget can expose an active concept or idea thread once the underlying analysis system is mature.

---

# Privacy Model

NOESIS treats thought data as highly sensitive by design.

The central architecture is:

```text id="shk4kb"
USER
 ↓
DEVICE
 ↓
ENCRYPTED DATABASE
 ↓
LOCAL ANALYSIS
```

Not:

```text id="0r0fx7"
USER
 ↓
REMOTE API
 ↓
CLOUD DATABASE
 ↓
ANALYSIS
```

The core application should function without:

* registration
* cloud synchronization
* remote AI
* advertising trackers
* analytics services

This allows NOESIS to remain useful even in a completely offline environment.

---

# Encryption

NOESIS should encrypt its local database from the beginning rather than adding encryption later.

The intended storage architecture is:

```text id="3p3jo7"
APPLICATION
     ↓
ROOM
     ↓
SQLCIPHER
     ↓
ENCRYPTED SQLITE
```

The encryption key should be protected through:

> **Android Keystore**

The application should avoid storing plaintext encryption secrets in SharedPreferences, ordinary files, or the database itself.

---

# Security Philosophy

NOESIS contains information that users may never want exposed.

Therefore the security model should avoid unnecessary duplication.

Sensitive thought content should not be copied into:

* debug logs
* analytics systems
* crash reports without careful filtering
* notification text by default
* unnecessary backups
* external APIs

When the application needs to reference an entry internally, it should prefer identifiers rather than duplicating the full text.

For example:

```text id="0q61x9"
N-0084
```

rather than repeatedly passing:

```text id="0v5o7t"
“I keep coming back to the idea...”
```

through unrelated subsystems.

---

# Local-First Architecture

NOESIS should be designed so that core capabilities can operate entirely on the device.

```text id="6kpj1s"
┌─────────────────────────────┐
│           NOESIS            │
├─────────────────────────────┤
│ Capture                     │
│ Archive                     │
│ Analysis                    │
│ Concepts                    │
│ Questions                   │
│ Revisions                   │
└─────────────┬───────────────┘
              │
              ↓
      ENCRYPTED LOCAL DB
              │
              ↓
       ANDROID KEYSTORE
```

Network access should not be required for basic operation.

---

# Data Model

A simplified model looks like:

```text id="2j72t6"
Entry
├── id
├── archiveNumber
├── createdAt
├── currentRevisionId
└── metadata

EntryRevision
├── id
├── entryId
├── revisionNumber
├── content
└── createdAt

Concept
├── id
├── name
├── normalizedTerms
└── createdAt

Observation
├── id
├── entryId
├── conceptId
├── evidence
└── confidence

Question
├── id
├── entryId
├── state
└── resolvedAt

Relation
├── sourceConcept
├── targetConcept
└── strength
```

The important architectural distinction is:

> **Entries and interpretations are separate entities.**

That allows the analytical engine to evolve without rewriting historical records.

---

# Analysis Pipeline

The first implementation can use a deterministic local pipeline.

```text id="j2ql1p"
RAW TEXT
   │
   ↓
TOKENIZE
   │
   ↓
NORMALIZE
   │
   ↓
STOP WORD FILTER
   │
   ↓
STEM
   │
   ↓
TERM FREQUENCY
   │
   ├── SINGLE TERMS
   │
   └── N-GRAM PHRASES
             │
             ↓
     CONCEPT CANDIDATES
             │
             ↓
       TIME ANALYSIS
             │
             ↓
        PERSISTENCE
             │
             ↓
        RELATIONSHIP
```

This approach is intentionally constrained.

It does not attempt to produce a synthetic personality model.

---

# Why No LLM for v1?

A language model can be extremely useful.

But it is not automatically the correct first solution.

NOESIS values:

* reproducibility
* privacy
* transparency
* low resource usage
* offline operation
* predictable behavior

A deterministic engine provides all five.

Later, optional semantic models can improve recognition of relationships that keyword methods cannot detect.

For example:

```text id="fgj8j7"
“building software”
```

and:

```text id="2y3vpu"
“creating digital tools”
```

could eventually be recognized as semantically related even though their exact words differ.

That belongs to a future semantic layer.

---

# Technology Stack

## Android

* Kotlin
* Jetpack Compose
* Android SDK
* Android Keystore
* Android App Widgets
* WorkManager

## Storage

* Room
* SQLCipher
* SQLite

## Analysis

* custom Kotlin NLP implementation
* deterministic tokenization
* stop-word filtering
* stemming
* n-gram phrase extraction
* local persistence analysis

## Future Research

* on-device embeddings
* semantic similarity
* optional local LLM
* concept graph visualization

---

# Interface and Design System

NOESIS is part of the broader **Institutum Null** software family.

The shared aesthetic is:

# **Occult Industrial Academia**

But NOESIS intentionally has its own atmosphere.

Where Index Prohibitorum feels like:

> a government security archive,

NOESIS feels like:

> **a forbidden research archive operated by philosophers and engineers.**

Its interface combines:

* brutalist archival systems
* scientific catalogues
* terminal interfaces
* scholarly typography
* manuscript conventions
* cognitive diagrams
* restrained data visualization

---

# Typography

Typography is intentionally dual-layered.

## Monospace

Used for:

* archive IDs
* timestamps
* metadata
* statistics
* technical labels
* record structures
* analysis output

Possible fonts:

* IBM Plex Mono
* JetBrains Mono

---

## Scholarly Serif

Used sparingly for:

* NOESIS titles
* conceptual headings
* institutional labels
* occasional literary statements

A font such as:

* Spectral
* EB Garamond
* IM FELL English

can provide the manuscript layer.

The serif should remain secondary.

The user's actual thoughts remain closer to the machine/archive layer.

---

# Color System

NOESIS intentionally avoids the amber warning palette of Index Prohibitorum.

Its signature is:

> **deep graphite + aged bone + muted indigo/violet**

Conceptually:

```text id="r7h9dr"
BACKGROUND
#08090C

SURFACE
#111219

TEXT
#D8D2C4

SECONDARY
#8D8A83

NOESIS
#8176A8

ACTIVE
#A79BCF

WARNING
#B56B63
```

The violet should remain restrained.

It should appear when something is cognitively significant rather than becoming the background color of the entire application.

---

# Interface Example

```text id="h46yw8"
╔════════════════════════════════════════╗
║ NOESIS                                 ║
║ ARCHIVE OF COGNITIVE RECORDS            ║
╠════════════════════════════════════════╣
║                                        ║
║ CAPTURE                                ║
║                                        ║
║ What is on your mind?                  ║
║                                        ║
║ > ____________________________________ ║
║ > ____________________________________ ║
║                                        ║
║                 [ ARCHIVE ]            ║
║                                        ║
╠════════════════════════════════════════╣
║ RECENT RECORDS                         ║
║                                        ║
║ N-0087  17:42                          ║
║ N-0086  15:03                          ║
║ N-0085  12:21                          ║
║                                        ║
╠════════════════════════════════════════╣
║ DETECTED                               ║
║                                        ║
║ PERSISTENT CONCEPT                     ║
║ CREATION                               ║
║ 12 observations                        ║
╚════════════════════════════════════════╝
```

The interface should feel like entering an archive rather than opening a social journal.

---

# Example Cognitive Thread

A typical NOESIS thread might evolve like this:

```text id="dpyqxp"
N-0017
“I want to learn more about Android.”

         ↓

N-0034
“Android architecture is way more
interesting than I expected.”

         ↓

N-0051
“I want SYSCPU to expose more
low-level information.”

         ↓

N-0068
“I keep thinking about software as
an instrument rather than an app.”

         ↓

N-0084
“I want to build more tools that
make invisible systems visible.”
```

NOESIS might identify:

```text id="ps9zec"
PERSISTENT IDEA

SYSTEM OBSERVATION

FIRST OBSERVED
14·07·2026

LATEST OBSERVED
12·08·2026

RELATED RECORDS
5

RELATED CONCEPTS

ANDROID
SOFTWARE
INSTRUMENTATION
VISIBILITY
```

The system does not tell the user what this means.

It simply makes the pattern visible.

---

# Installation

## Requirements

Typical development requirements include:

* Android Studio
* recent Android SDK
* Kotlin-compatible JDK
* Gradle
* Git

Verify the environment:

```bash id="wuxz0t"
java --version
git --version
```

---

# Clone

```bash id="k6o4z4"
git clone https://github.com/Onesoulmoon/NOESIS.git
cd NOESIS
```

Replace the repository URL if the repository name changes.

---

# Build

Open the project in Android Studio and allow Gradle synchronization to complete.

Then:

```bash id="c0bj6p"
./gradlew assembleDebug
```

On Windows:

```powershell id="p3yur5"
gradlew.bat assembleDebug
```

The resulting APK will normally appear under:

```text id="z3z1ay"
app/build/outputs/apk/debug/
```

---

# Storage and Data

NOESIS's primary user data is the encrypted local archive.

Core data includes:

* entries
* revisions
* concepts
* relationships
* questions
* observation metadata
* analysis state

The application should never depend on a remote database for the archive to exist.

The local archive is the canonical source.

---

# Backup Philosophy

Because NOESIS contains sensitive material, backup should be explicit.

A future backup system should distinguish:

### Archive backup

Encrypted database backup.

### Export

User-selected human-readable or machine-readable data.

### Full recovery

Encrypted archive + key recovery mechanism.

The application should not silently upload an archive simply because Android offers cloud backup facilities.

Backup behavior should be transparent and documented.

---

# Limitations

NOESIS is intentionally limited.

## It does not understand the user.

It detects textual patterns.

That is not the same thing as understanding a person's mind.

---

## It cannot determine psychological truth.

Repeated use of a word does not prove psychological significance.

---

## Keyword analysis has limits.

Two semantically identical statements can use completely different vocabulary.

This is one reason future semantic analysis may be valuable.

---

## False positives are possible.

A recurring concept may be statistically persistent but personally unimportant.

---

## False negatives are possible.

An important idea expressed using changing language may evade deterministic matching.

---

## Language matters.

Stemming and stop-word analysis are language-dependent.

The architecture should therefore support language-specific analyzers rather than assuming that one NLP pipeline works equally well for every language.

---

# Roadmap

## Phase I — Cognitive Archive

* [ ] Capture-first interface
* [ ] Encrypted Room / SQLCipher database
* [ ] Android Keystore integration
* [ ] Global archive numbering
* [ ] Entry revisions
* [ ] Voice-to-text capture
* [ ] Recent stream
* [ ] Basic analysis window

---

## Phase II — Pattern Detection

* [ ] Tokenization
* [ ] Stop-word filtering
* [ ] Stemming
* [ ] Keyword extraction
* [ ] N-gram phrase detection
* [ ] Concept candidates
* [ ] Recurrence detection
* [ ] Persistence scoring
* [ ] Explainable evidence

---

## Phase III — Cognitive Threads

* [ ] Persistent ideas
* [ ] Deeply persistent ideas
* [ ] Unresolved questions
* [ ] Concept relationships
* [ ] Thread history
* [ ] Resurfacing detection
* [ ] Cross-record exploration

---

## Phase IV — Resonance

* [ ] Constellation map
* [ ] Concept graph
* [ ] Relationship strength
* [ ] Temporal visualization
* [ ] Concept evolution
* [ ] Cognitive timeline
* [ ] Archive-wide resonance analysis

---

## Phase V — Optional Semantic Intelligence

Potential future functionality:

* [ ] On-device embeddings
* [ ] Semantic similarity
* [ ] Local topic clustering
* [ ] Optional local language model
* [ ] AI-assisted concept suggestions
* [ ] User-confirmed semantic relationships

Any intelligent analysis should remain subordinate to the archive.

AI output should never silently modify the underlying records.

---

# Contributing

Contributions are welcome.

Especially valuable contributions include:

* Android compatibility
* database design
* cryptographic review
* deterministic NLP algorithms
* multilingual analysis
* accessibility
* UI performance
* widget development
* testing
* documentation

Before implementing an analytical feature, consider:

> **Can the user understand why the software reached this conclusion?**

If not, the feature may need a more transparent design.

---

# Development Principles

### Preserve the raw record.

Never overwrite the user's source material with machine interpretation.

### Make inference visible.

If a pattern was detected, show the evidence.

### Keep the core local.

The archive should not depend on the cloud.

### Use AI carefully.

Do not introduce a large model merely because it makes a demo look smarter.

### Respect uncertainty.

“Unknown” can be more truthful than a confident answer.

### Minimize friction.

Recording a thought should take seconds.

### Never weaponize introspection.

NOESIS should not manipulate users with artificial psychological insights, engagement loops, or guilt-driven notifications.

---

# Philosophy

NOESIS is built around a simple observation:

> **Most thoughts disappear before we understand why they mattered.**

A person may think about an idea once.

Then again three weeks later.

Then again six months afterward.

Those thoughts may look unrelated when viewed individually.

Only time reveals the relationship.

Traditional journaling preserves the fragments.

NOESIS attempts to preserve the **continuity between fragments**.

```text id="8fv2uw"
THOUGHT
   ↓
RECORD
   ↓
TIME
   ↓
REPETITION
   ↓
RELATION
   ↓
PERSISTENCE
   ↓
IDEA
```

The software does not manufacture meaning.

It attempts to expose **patterns that already exist inside the archive**.

---

# The Difference Between Memory and NOESIS

A conventional notes application says:

> “Here is what you wrote.”

NOESIS eventually aims to say:

> “Here is what you wrote, how it changed, what it repeatedly touched, what remained unresolved, and which ideas survived long enough to become persistent.”

That is a fundamentally different relationship with personal information.

---

# The Archive Is Not the Mind

NOESIS should never pretend otherwise.

The application only sees what the user chooses to record.

A person is vastly more complicated than:

* words
* timestamps
* concepts
* frequencies
* relationships

The archive is therefore not a digital copy of the mind.

It is:

> **a partial fossil record of what the user chose to preserve.**

That limitation is not a defect.

It is part of the philosophy.

---

# INSTITUTUM NULL

Within the wider **Index Scientiarum Interdictarum** ecosystem, NOESIS represents the cognitive division of the archive.

```text id="m6d2bb"
INSTITUTUM NULL

COGNITIVE ARCHIVE
       │
       └── NOESIS

The system observes:

what was written,
what returned,
what persisted,
what changed,
what remained unresolved.
```

NOESIS therefore sits beside projects such as **Index Prohibitorum** and **Necroware**, but its subject is different.

Index Prohibitorum examines the machine.

Necroware examines abandoned digital remains.

NOESIS examines the **history of thought contained within deliberate records**.

---

# Project Status

**In Development / Experimental**

NOESIS is an evolving open-source project.

The current priority is the creation of a robust, private, deterministic cognitive archive before adding more sophisticated semantic intelligence.

The project favors:

**privacy over convenience**

**explainability over artificial intelligence**

**history over deletion**

**observation over interpretation**

**human control over automation**

---

# Author

**Souleymane Mountaga WONE**

Independent developer and designer.

Senegal.

GitHub:

**[@Onesoulmoon](https://github.com/Onesoulmoon)**

---

# Final Principle

```text id="s2qjqh"
RECORD WHAT YOU THINK.

PRESERVE WHAT YOU SAID.

OBSERVE WHAT RETURNS.

QUESTION WHAT PERSISTS.

DO NOT CONFUSE THE ARCHIVE
WITH THE MIND.
```

> **NOESIS**
>
> *An archive does not tell you who you are.*
>
> *It shows you what you have chosen not to forget.*
