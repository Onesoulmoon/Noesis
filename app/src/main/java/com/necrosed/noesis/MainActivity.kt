package com.necrosed.noesis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import com.necrosed.noesis.ui.MainScreen
import com.necrosed.noesis.ui.MainViewModel
import com.necrosed.noesis.ui.theme.NoesisTheme

// ═══════════════════════════════════════════════════════════════
// NOESIS — MAIN ACTIVITY
//
// Single activity. Edge-to-edge. Dark status bar.
//
// GOOGLE FONTS SETUP NOTE:
// Type.kt uses Spectral + IBM Plex Mono via Google Fonts provider.
// On first build, Android Studio may warn about missing
// com_google_android_gms_fonts_certs.xml.
//
// To generate it automatically:
//   Resources > Add Google Font > (choose any font) > OK
//   Android Studio creates the certs file automatically.
//   You can then remove the font you added and keep the certs.
//
// OR add manually: create res/values/font_certs.xml with the
// standard Google GMS fonts certificates array
// (available from Google's developer docs).
//
// If you skip this step, the app will fall back to the system
// default monospace and serif fonts — fully functional.
// ═══════════════════════════════════════════════════════════════

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            NoesisTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
