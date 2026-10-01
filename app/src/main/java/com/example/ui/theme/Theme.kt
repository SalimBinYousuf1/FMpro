package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat
import com.example.data.repository.EditorFont
import com.example.data.repository.EditorSettings
import com.example.data.repository.EditorThemeMode

data class SalimCustomColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val secondarySurface: Color,
    val cardBorder: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val selection: Color
)

val LightCustomColors = SalimCustomColors(
    isDark = false,
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    secondarySurface = Color(0xFFF1F3F5),
    cardBorder = Color(0xFFE9ECEF),
    divider = Color(0xFFDEE2E6),
    textPrimary = Color(0xFF212529),
    textSecondary = Color(0xFF6C757D),
    textTertiary = Color(0xFFADB5BD),
    accent = SalimBlue,
    selection = Color(0x33007AFF)
)

val DarkCustomColors = SalimCustomColors(
    isDark = true,
    background = Color(0xFF121214),
    surface = Color(0xFF1E1E22),
    secondarySurface = Color(0xFF28282E),
    cardBorder = Color(0xFF33333A),
    divider = Color(0xFF2C2C34),
    textPrimary = Color(0xFFF8F9FA),
    textSecondary = Color(0xFFA0A0AB),
    textTertiary = Color(0xFF6C757D),
    accent = SalimBlue,
    selection = Color(0x40007AFF)
)

val OledCustomColors = SalimCustomColors(
    isDark = true,
    background = Color(0xFF000000),
    surface = Color(0xFF0D0D0E),
    secondarySurface = Color(0xFF18181A),
    cardBorder = Color(0xFF222226),
    divider = Color(0xFF1E1E22),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFA0A0AB),
    textTertiary = Color(0xFF66666E),
    accent = SalimBlue,
    selection = Color(0x40007AFF)
)

val LocalEditorColors = staticCompositionLocalOf { LightCustomColors }
val LocalEditorSettings = staticCompositionLocalOf { EditorSettings() }

val SalimThemeColors: SalimCustomColors
    get() = LightCustomColors

fun parseHexColor(hex: String, default: Color = SalimBlue): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = if (clean.length == 6) {
            (0xFF000000 or clean.toLong(16)).toInt()
        } else if (clean.length == 8) {
            clean.toLong(16).toInt()
        } else {
            return default
        }
        Color(colorInt)
    } catch (_: Exception) {
        default
    }
}

fun getFontFamily(font: EditorFont): FontFamily {
    return when (font) {
        EditorFont.MONOSPACE -> FontFamily.Monospace
        EditorFont.SERIF -> FontFamily.Serif
        EditorFont.SANS_SERIF -> FontFamily.SansSerif
        EditorFont.JETBRAINS_MONO -> FontFamily.Monospace
        EditorFont.FIRA_CODE -> FontFamily.Monospace
        EditorFont.SYSTEM_DEFAULT -> FontFamily.Default
    }
}

@Composable
fun SalimAppTheme(
    settings: EditorSettings = EditorSettings(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isDark = when (settings.themeMode) {
        EditorThemeMode.LIGHT -> false
        EditorThemeMode.DARK -> true
        EditorThemeMode.SYSTEM -> darkTheme
        EditorThemeMode.OLED -> true
    }

    val baseColors = when {
        settings.themeMode == EditorThemeMode.OLED -> OledCustomColors
        isDark -> DarkCustomColors
        else -> LightCustomColors
    }

    val accent = parseHexColor(settings.accentColorHex, SalimBlue)
    val customColors = baseColors.copy(accent = accent)

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accent,
            background = customColors.background,
            surface = customColors.surface,
            onPrimary = Color.White,
            onBackground = customColors.textPrimary,
            onSurface = customColors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = accent,
            background = customColors.background,
            surface = customColors.surface,
            onPrimary = Color.White,
            onBackground = customColors.textPrimary,
            onSurface = customColors.textPrimary
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = customColors.background.toArgb()
                window.navigationBarColor = customColors.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalEditorColors provides customColors,
        LocalEditorSettings provides settings
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
