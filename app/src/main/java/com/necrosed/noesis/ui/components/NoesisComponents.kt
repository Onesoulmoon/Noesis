package com.necrosed.noesis.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.necrosed.noesis.data.model.*
import com.necrosed.noesis.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════════════════
// NOESIS — COMPONENT LIBRARY
//
// Aesthetic: Forbidden research archive.
// Dual type system: Spectral for identity, PlexMono for data.
// Violet appears only when cognitively significant.
// ═══════════════════════════════════════════════════════════════

// ─── SCANLINES ──────────────────────────────────────────────────

fun Modifier.noesisScanlines(alpha: Float = 0.025f): Modifier = this.drawWithContent {
    drawContent()
    val lineH   = 1.dp.toPx()
    val spacing = 5.dp.toPx()
    var y = 0f
    while (y < size.height) {
        drawRect(Color.Black.copy(alpha = alpha), Offset(0f, y), Size(size.width, lineH))
        y += spacing
    }
}

// ─── MANUSCRIPT CORNER MARKS ────────────────────────────────────
// Lighter and more refined than INDEX PROHIBITORUM brackets —
// feels like pressed document registration marks

fun Modifier.manuscriptCorners(
    color: Color = BorderLight,
    size: Dp = 8.dp,
    sw: Dp = Dp(0.5f)
): Modifier = this.drawBehind {
    val s = size.toPx(); val sw2 = sw.toPx()
    val w = this.size.width; val h = this.size.height
    drawLine(color, Offset(0f, s), Offset(0f, 0f), sw2)
    drawLine(color, Offset(0f, 0f), Offset(s, 0f), sw2)
    drawLine(color, Offset(w - s, 0f), Offset(w, 0f), sw2)
    drawLine(color, Offset(w, 0f), Offset(w, s), sw2)
    drawLine(color, Offset(0f, h - s), Offset(0f, h), sw2)
    drawLine(color, Offset(0f, h), Offset(s, h), sw2)
    drawLine(color, Offset(w - s, h), Offset(w, h), sw2)
    drawLine(color, Offset(w, h - s), Offset(w, h), sw2)
}

// ─── DIVIDERS ───────────────────────────────────────────────────

@Composable
fun NoesisDivider(
    modifier: Modifier = Modifier,
    color: Color = BorderLight,
    thickness: Dp = Dp(0.5f)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@Composable
fun NoesisDottedRule(
    modifier: Modifier = Modifier,
    color: Color = BorderFaint
) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(8.dp)
        .drawBehind {
            val dw = 2.dp.toPx(); val gap = 5.dp.toPx()
            var x = 0f; val cy = size.height / 2f
            while (x < size.width) {
                drawRect(color, Offset(x, cy - 0.5f), Size(dw, 1.dp.toPx()))
                x += dw + gap
            }
        })
}

// ─── SECTION HEADER ─────────────────────────────────────────────

@Composable
fun NoesisSectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        NoesisDivider()
        Spacer(Modifier.height(5.dp))
        Text(title, style = NoesisSectionHeader)
        subtitle?.let {
            Text(it, style = NoesisMicro.copy(color = NoesisGhostText))
        }
        Spacer(Modifier.height(5.dp))
    }
}

// ─── PERSISTENCE BADGE ──────────────────────────────────────────

@Composable
fun PersistenceBadge(
    level: PLevel,
    modifier: Modifier = Modifier
) {
    if (level == PLevel.NONE) return
    val (label, color) = when (level) {
        PLevel.DEEPLY_PERSISTENT -> "DEEPLY PERSISTENT" to NoesisVioletHi
        PLevel.PERSISTENT        -> "PERSISTENT"        to NoesisViolet
        PLevel.RECURRING         -> "RECURRING"         to NoesisIvory
        PLevel.NONE              -> return
    }
    Text(
        text = label,
        style = NoesisBadge.copy(color = color, letterSpacing = 2.sp),
        modifier = modifier
            .border(Dp(0.5f), color.copy(alpha = 0.5f))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    )
}

// ─── CONCEPT STRENGTH BAR ───────────────────────────────────────

@Composable
fun ConceptStrengthBar(
    value: Int,      // 0–100 (observation count or confidence)
    max: Int = 100,
    modifier: Modifier = Modifier,
    color: Color = NoesisViolet
) {
    val filled = ((value.toFloat() / max.coerceAtLeast(1)) * 10).toInt().coerceIn(0, 10)
    val empty  = 10 - filled
    Text(
        text  = "▪".repeat(filled) + "·".repeat(empty),
        style = NoesisMono.copy(
            color         = color,
            letterSpacing = 1.sp,
            fontSize      = 11.sp,
            fontFamily    = PlexMono
        ),
        modifier = modifier
    )
}

// ─── ENTRY CARD ─────────────────────────────────────────────────

@Composable
fun EntryCard(
    entry: Entry,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showConcepts: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .border(Dp(0.5f), BorderLight)
            .manuscriptCorners(color = BorderMid)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {

            // ID + timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text  = entry.displayId,
                    style = NoesisMeta.copy(color = NoesisVioletDim)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (entry.isUnresolved) {
                        Text(
                            text  = "OPEN",
                            style = NoesisBadge.copy(color = NoesisWarning),
                            modifier = Modifier
                                .border(Dp(0.5f), NoesisWarning.copy(alpha = 0.4f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        text  = formatEntryDate(entry.createdAt),
                        style = NoesisMicro
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Entry content — the sacred original text
            Text(
                text     = entry.content,
                style    = NoesisEntryBody,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            // Concept links
            if (showConcepts && entry.conceptLinks.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                NoesisDottedRule()
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    entry.conceptLinks.take(3).forEach { link ->
                        ConceptChip(label = link.label)
                    }
                    if (entry.conceptLinks.size > 3) {
                        Text(
                            text  = "+${entry.conceptLinks.size - 3}",
                            style = NoesisMicro.copy(color = NoesisVioletDim)
                        )
                    }
                }
            }
        }
    }
}

// ─── CONCEPT CHIP ───────────────────────────────────────────────

@Composable
fun ConceptChip(label: String, modifier: Modifier = Modifier) {
    Text(
        text  = label,
        style = NoesisMicro.copy(color = NoesisVioletDim, letterSpacing = 1.sp),
        modifier = modifier
            .border(Dp(0.5f), NoesisVioletDim.copy(alpha = 0.4f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    )
}

// ─── CONCEPT CARD ───────────────────────────────────────────────

@Composable
fun ConceptCard(
    concept: Concept,
    rank: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val accentColor = when (concept.persistenceLevel) {
        PLevel.DEEPLY_PERSISTENT -> NoesisVioletHi
        PLevel.PERSISTENT        -> NoesisViolet
        PLevel.RECURRING         -> NoesisIvory
        PLevel.NONE              -> NoesisGray
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(NoesisPanel)
            .background(
                if (concept.persistenceLevel == PLevel.DEEPLY_PERSISTENT)
                    NoesisVioletVeil.copy(alpha = 0.4f) else Color.Transparent
            )
            .border(Dp(0.5f), accentColor.copy(alpha = 0.25f))
            .manuscriptCorners(color = accentColor.copy(alpha = 0.4f))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Rank number
            Text(
                text  = rank.toString().padStart(2, '0'),
                style = NoesisMeta.copy(color = accentColor, fontSize = 16.sp),
                modifier = Modifier.width(26.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                // Concept label — Spectral serif
                Text(
                    text  = concept.label,
                    style = NoesisConceptTitle.copy(color = NoesisBone, fontSize = 15.sp)
                )

                Spacer(Modifier.height(4.dp))

                // Stats row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "${concept.observationCount} obs",
                        style = NoesisMicro.copy(color = NoesisGray)
                    )
                    if (concept.spanDays > 0) {
                        Text(
                            text  = "${concept.spanDays}d span",
                            style = NoesisMicro.copy(color = NoesisGrayDim)
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                ConceptStrengthBar(
                    value = concept.observationCount.coerceAtMost(10),
                    max   = 10,
                    color = accentColor
                )
            }

            // Persistence badge
            PersistenceBadge(concept.persistenceLevel)
        }
    }
}

// ─── ARCHIVE STAT ROW ───────────────────────────────────────────

@Composable
fun ArchiveStatRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text  = label,
            style = NoesisLabel,
            modifier = Modifier.width(120.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
                .height(8.dp)
                .drawBehind {
                    var x = 0f
                    val cy = size.height / 2f
                    val dotW = 2.dp.toPx(); val gap = 4.dp.toPx()
                    while (x < size.width) {
                        drawRect(BorderFaint, Offset(x, cy), Size(dotW, 1.dp.toPx()))
                        x += dotW + gap
                    }
                }
        )
        Text(text = value, style = NoesisData)
    }
}

// ─── TYPEWRITER TEXT ────────────────────────────────────────────

@Composable
fun NoesisTypewriter(
    text: String,
    style: androidx.compose.ui.text.TextStyle = NoesisEntryBody,
    delayMs: Long = 25,
    modifier: Modifier = Modifier
) {
    var displayed by remember { mutableStateOf("") }
    LaunchedEffect(text) {
        displayed = ""
        text.forEach { char -> displayed += char; delay(delayMs) }
    }
    Text(text = displayed, style = style, modifier = modifier)
}

// ─── BLINKING CURSOR ────────────────────────────────────────────

@Composable
fun NoesisCursor(modifier: Modifier = Modifier, color: Color = NoesisViolet) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { while (true) { delay(530); visible = !visible } }
    Text(
        text  = if (visible) "█" else " ",
        style = NoesisEntryBody.copy(color = color),
        modifier = modifier
    )
}

// ─── ACTION BUTTON ──────────────────────────────────────────────

@Composable
fun NoesisButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NoesisViolet,
    enabled: Boolean = true
) {
    val c = if (enabled) color else NoesisGrayDim
    Box(
        modifier = modifier
            .border(Dp(0.5f), c.copy(alpha = 0.7f))
            .background(c.copy(alpha = 0.06f))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = "[ $label ]",
            style = NoesisLabel.copy(color = c, letterSpacing = 2.sp)
        )
    }
}

// ─── EMPTY STATE ────────────────────────────────────────────────

@Composable
fun NoesisEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text  = "◇",
            style = NoesisConceptTitle.copy(color = NoesisGhostText, fontSize = 28.sp),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text  = title,
            style = NoesisSectionHeader.copy(color = NoesisGrayDim),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = subtitle,
            style = NoesisMicro.copy(color = NoesisGhostText),
            textAlign = TextAlign.Center
        )
    }
}

// ─── LOADING ────────────────────────────────────────────────────

@Composable
fun NoesisLoading(modifier: Modifier = Modifier) {
    var progress by remember { mutableIntStateOf(0) }
    val messages = listOf(
        "OPENING ARCHIVE...",
        "DECRYPTING RECORDS...",
        "LOADING CONCEPT INDEX...",
        "CALCULATING PERSISTENCE...",
        "ARCHIVE READY."
    )
    var msgIdx by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (progress < 100) {
            delay(30); progress = (progress + 3).coerceAtMost(100)
            msgIdx = ((progress / 100f) * (messages.size - 1)).toInt()
        }
    }
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("NOESIS", style = NoesisWordmark)
        Text(
            text  = "COGNITIVE ARCHIVE",
            style = NoesisSectionHeader.copy(color = NoesisGrayDim, letterSpacing = 4.sp)
        )
        Spacer(Modifier.height(32.dp))
        NoesisDivider()
        Spacer(Modifier.height(20.dp))
        Text(messages.getOrElse(msgIdx) { "" }, style = NoesisMeta.copy(color = NoesisVioletDim))
        Spacer(Modifier.height(10.dp))
        val filled = (progress / 10).coerceIn(0, 10)
        Text(
            text  = "▪".repeat(filled) + "·".repeat(10 - filled) + "  $progress%",
            style = NoesisMono.copy(color = NoesisViolet, fontFamily = PlexMono)
        )
    }
}

// ─── HELPERS ────────────────────────────────────────────────────

private val dateFormat     = SimpleDateFormat("dd·MMM·yyyy", Locale.getDefault())
private val dateTimeFormat = SimpleDateFormat("dd·MMM  HH:mm", Locale.getDefault())
private val timeFormat     = SimpleDateFormat("HH:mm", Locale.getDefault())

fun formatEntryDate(ms: Long): String {
    val now  = System.currentTimeMillis()
    val diff = now - ms
    val days = diff / 86_400_000L
    return when {
        diff < 60_000L    -> "JUST NOW"
        diff < 3_600_000L -> "${diff / 60_000L}m AGO"
        diff < 86_400_000L -> timeFormat.format(Date(ms))
        days < 7          -> "${days}d AGO"
        else              -> dateFormat.format(Date(ms))
    }
}

fun formatFullDate(ms: Long): String = dateTimeFormat.format(Date(ms)).uppercase()

fun persistenceLevelColor(level: PLevel): Color = when (level) {
    PLevel.DEEPLY_PERSISTENT -> NoesisVioletHi
    PLevel.PERSISTENT        -> NoesisViolet
    PLevel.RECURRING         -> NoesisIvory
    PLevel.NONE              -> NoesisGrayDim
}

// Needed by screens that import MonoFontFamily-style
val NoesisMono = NoesisData
