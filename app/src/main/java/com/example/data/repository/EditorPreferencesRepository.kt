package com.example.data.repository

import android.content.Context
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class EditorThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
    OLED
}

enum class EditorFont(val displayName: String) {
    SYSTEM_DEFAULT("System"),
    MONOSPACE("Monospace"),
    SERIF("Serif"),
    SANS_SERIF("Sans Serif"),
    JETBRAINS_MONO("JetBrains Mono"),
    FIRA_CODE("Fira Code")
}

enum class EditorMargin(val displayName: String) {
    NARROW("Narrow (8dp)"),
    NORMAL("Normal (16dp)"),
    WIDE("Wide (24dp)")
}

enum class NavigationPosition {
    BOTTOM,
    TOP,
    SIDE
}

enum class EditorPreset(val displayName: String) {
    WRITER("Focus Writer"),
    CODER("Code Editor"),
    MINIMAL("Minimalist"),
    CUSTOM("Custom")
}

data class EditorSettings(
    val themeMode: EditorThemeMode = EditorThemeMode.SYSTEM,
    val accentColorHex: String = "#007AFF",
    val font: EditorFont = EditorFont.MONOSPACE,
    val fontSize: Float = 14f,
    val lineHeight: Float = 1.4f,
    val margin: EditorMargin = EditorMargin.NORMAL,
    val showLineNumbers: Boolean = true,
    val wordWrap: Boolean = false,
    val navigationPosition: NavigationPosition = NavigationPosition.BOTTOM,
    val quickAccessoryBar: Boolean = true,
    val gesturePinchZoom: Boolean = true,
    val gestureTwoFingerUndo: Boolean = true,
    val hapticFeedback: Boolean = true,
    val activePreset: EditorPreset = EditorPreset.CODER
)

class EditorPreferencesRepository(context: Context) {

    private val prefs = context.getSharedPreferences("salim_editor_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: Flow<EditorSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): EditorSettings {
        val themeModeName = prefs.getString("theme_mode", EditorThemeMode.SYSTEM.name) ?: EditorThemeMode.SYSTEM.name
        val fontName = prefs.getString("font", EditorFont.MONOSPACE.name) ?: EditorFont.MONOSPACE.name
        val marginName = prefs.getString("margin", EditorMargin.NORMAL.name) ?: EditorMargin.NORMAL.name
        val navPosName = prefs.getString("nav_pos", NavigationPosition.BOTTOM.name) ?: NavigationPosition.BOTTOM.name
        val presetName = prefs.getString("preset", EditorPreset.CODER.name) ?: EditorPreset.CODER.name

        return EditorSettings(
            themeMode = runCatching { EditorThemeMode.valueOf(themeModeName) }.getOrDefault(EditorThemeMode.SYSTEM),
            accentColorHex = prefs.getString("accent_color", "#007AFF") ?: "#007AFF",
            font = runCatching { EditorFont.valueOf(fontName) }.getOrDefault(EditorFont.MONOSPACE),
            fontSize = prefs.getFloat("font_size", 14f),
            lineHeight = prefs.getFloat("line_height", 1.4f),
            margin = runCatching { EditorMargin.valueOf(marginName) }.getOrDefault(EditorMargin.NORMAL),
            showLineNumbers = prefs.getBoolean("show_line_numbers", true),
            wordWrap = prefs.getBoolean("word_wrap", false),
            navigationPosition = runCatching { NavigationPosition.valueOf(navPosName) }.getOrDefault(NavigationPosition.BOTTOM),
            quickAccessoryBar = prefs.getBoolean("quick_bar", true),
            gesturePinchZoom = prefs.getBoolean("pinch_zoom", true),
            gestureTwoFingerUndo = prefs.getBoolean("two_finger_undo", true),
            hapticFeedback = prefs.getBoolean("haptic", true),
            activePreset = runCatching { EditorPreset.valueOf(presetName) }.getOrDefault(EditorPreset.CODER)
        )
    }

    private fun saveSettings(settings: EditorSettings) {
        prefs.edit().apply {
            putString("theme_mode", settings.themeMode.name)
            putString("accent_color", settings.accentColorHex)
            putString("font", settings.font.name)
            putFloat("font_size", settings.fontSize)
            putFloat("line_height", settings.lineHeight)
            putString("margin", settings.margin.name)
            putBoolean("show_line_numbers", settings.showLineNumbers)
            putBoolean("word_wrap", settings.wordWrap)
            putString("nav_pos", settings.navigationPosition.name)
            putBoolean("quick_bar", settings.quickAccessoryBar)
            putBoolean("pinch_zoom", settings.gesturePinchZoom)
            putBoolean("two_finger_undo", settings.gestureTwoFingerUndo)
            putBoolean("haptic", settings.hapticFeedback)
            putString("preset", settings.activePreset.name)
            apply()
        }
        _settingsFlow.value = settings
    }

    suspend fun setThemeMode(mode: EditorThemeMode) {
        _settingsFlow.update { current ->
            val updated = current.copy(themeMode = mode, activePreset = EditorPreset.CUSTOM)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setAccentColor(hex: String) {
        _settingsFlow.update { current ->
            val updated = current.copy(accentColorHex = hex, activePreset = EditorPreset.CUSTOM)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setFont(font: EditorFont) {
        _settingsFlow.update { current ->
            val updated = current.copy(font = font, activePreset = EditorPreset.CUSTOM)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setFontSize(size: Float) {
        _settingsFlow.update { current ->
            val updated = current.copy(fontSize = size.coerceIn(8f, 32f))
            saveSettings(updated)
            updated
        }
    }

    suspend fun setLineHeight(multiplier: Float) {
        _settingsFlow.update { current ->
            val updated = current.copy(lineHeight = multiplier.coerceIn(1.0f, 2.5f))
            saveSettings(updated)
            updated
        }
    }

    suspend fun setMargin(margin: EditorMargin) {
        _settingsFlow.update { current ->
            val updated = current.copy(margin = margin, activePreset = EditorPreset.CUSTOM)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setShowLineNumbers(enabled: Boolean) {
        _settingsFlow.update { current ->
            val updated = current.copy(showLineNumbers = enabled)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setWordWrap(enabled: Boolean) {
        _settingsFlow.update { current ->
            val updated = current.copy(wordWrap = enabled)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setNavigationPosition(pos: NavigationPosition) {
        _settingsFlow.update { current ->
            val updated = current.copy(navigationPosition = pos)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setQuickAccessoryBar(enabled: Boolean) {
        _settingsFlow.update { current ->
            val updated = current.copy(quickAccessoryBar = enabled)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setGesturePinchZoom(enabled: Boolean) {
        _settingsFlow.update { current ->
            val updated = current.copy(gesturePinchZoom = enabled)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setGestureTwoFingerUndo(enabled: Boolean) {
        _settingsFlow.update { current ->
            val updated = current.copy(gestureTwoFingerUndo = enabled)
            saveSettings(updated)
            updated
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        _settingsFlow.update { current ->
            val updated = current.copy(hapticFeedback = enabled)
            saveSettings(updated)
            updated
        }
    }

    suspend fun applyPreset(preset: EditorPreset) {
        _settingsFlow.update { current ->
            val updated = when (preset) {
                EditorPreset.WRITER -> current.copy(
                    activePreset = preset,
                    font = EditorFont.SERIF,
                    fontSize = 17f,
                    lineHeight = 1.6f,
                    showLineNumbers = false,
                    wordWrap = true,
                    margin = EditorMargin.WIDE
                )
                EditorPreset.CODER -> current.copy(
                    activePreset = preset,
                    font = EditorFont.MONOSPACE,
                    fontSize = 13f,
                    lineHeight = 1.35f,
                    showLineNumbers = true,
                    wordWrap = false,
                    margin = EditorMargin.NARROW
                )
                EditorPreset.MINIMAL -> current.copy(
                    activePreset = preset,
                    font = EditorFont.SANS_SERIF,
                    fontSize = 15f,
                    lineHeight = 1.5f,
                    showLineNumbers = false,
                    wordWrap = true,
                    margin = EditorMargin.NORMAL
                )
                EditorPreset.CUSTOM -> current.copy(activePreset = preset)
            }
            saveSettings(updated)
            updated
        }
    }
}
