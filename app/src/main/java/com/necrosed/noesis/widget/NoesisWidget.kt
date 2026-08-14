package com.necrosed.noesis.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.necrosed.noesis.MainActivity
import com.necrosed.noesis.data.db.NoesisDatabase

// ═══════════════════════════════════════════════════════════════
// NOESIS TERMINAL WIDGET
//
// ┌─────────────────────────────┐
// │ NOESIS                      │
// │ COGNITIVE ARCHIVE           │
// │                             │
// │ ACTIVE       047            │
// │ PERSISTENT   008            │
// │ UNRESOLVED   019            │
// │                             │
// │ [ + CAPTURE ]               │
// └─────────────────────────────┘
//
// Tapping [ + CAPTURE ] opens directly to the capture screen.
// Widget refreshes every 30 minutes via updatePeriodMillis.
// ═══════════════════════════════════════════════════════════════

class NoesisWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read from encrypted DB — runs in Glance's coroutine context
        val db          = NoesisDatabase.getInstance(context)
        val active      = db.entryDao().getActiveCount()
        val persistent  = db.conceptDao().getWidgetPersistentCount()
        val unresolved  = db.entryDao().getUnresolvedCount()

        provideContent {
            WidgetContent(active, persistent, unresolved)
        }
    }
}

@Composable
private fun WidgetContent(active: Int, persistent: Int, unresolved: Int) {
    val bgColor        = GlanceTheme.colors.background
    val violet         = ColorProvider(Color(0xFF8176A8))
    val violetHi       = ColorProvider(Color(0xFFA79BCF))
    val bone           = ColorProvider(Color(0xFFD8D2C4))
    val gray           = ColorProvider(Color(0xFF8D8A83))
    val warning        = ColorProvider(Color(0xFFB56B63))
    val ghostText      = ColorProvider(Color(0xFF3A3830))
    val panelBg        = ColorProvider(Color(0xFF111219))

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF111219))
            .padding(14.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            // Wordmark
            Text(
                text  = "NOESIS",
                style = TextStyle(
                    color      = violetHi,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text  = "COGNITIVE ARCHIVE",
                style = TextStyle(
                    color    = gray,
                    fontSize = 8.sp
                )
            )

            Spacer(GlanceModifier.height(10.dp))

            // Divider approximation
            Box(GlanceModifier.fillMaxWidth().height(0.5.dp).background(Color(0xFF252830))) {}

            Spacer(GlanceModifier.height(10.dp))

            // Stats
            WidgetStat(label = "ACTIVE",     value = active.toString(),     color = bone)
            WidgetStat(label = "PERSISTENT", value = persistent.toString(), color = violetHi)
            WidgetStat(label = "UNRESOLVED", value = unresolved.toString(), color = warning)

            Spacer(GlanceModifier.defaultWeight())

            // Capture button — taps open MainActivity
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1828))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .clickable(actionStartActivity<MainActivity>()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = "[ + CAPTURE ]",
                    style = TextStyle(
                        color      = violet,
                        fontSize   = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(GlanceModifier.height(4.dp))
            Text(
                text  = "REF:NΩ-001",
                style = TextStyle(color = ghostText, fontSize = 7.sp)
            )
        }
    }
}

@Composable
private fun WidgetStat(
    label: String,
    value: String,
    color: ColorProvider
) {
    Row(
        modifier             = GlanceModifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalAlignment  = Alignment.Start,
        verticalAlignment    = Alignment.CenterVertically
    ) {
        Text(
            text  = label,
            style = TextStyle(color = ColorProvider(Color(0xFF5A5850)), fontSize = 9.sp),
            modifier = GlanceModifier.width(80.dp)
        )
        Text(
            text  = value.padStart(3),
            style = TextStyle(
                color      = color,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// ─── WIDGET RECEIVER ────────────────────────────────────────────

class NoesisWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = NoesisWidget()
}

// ─── BOOT RECEIVER ──────────────────────────────────────────────

class BootReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Widget update is handled by system via updatePeriodMillis
            // This receiver exists for future scheduling extensions
        }
    }
}
