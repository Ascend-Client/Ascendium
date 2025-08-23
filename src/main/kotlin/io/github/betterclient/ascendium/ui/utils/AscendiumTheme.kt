package io.github.betterclient.ascendium.ui.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import io.github.betterclient.ascendium.Ascendium

@Composable
fun AscendiumTheme(content: @Composable () -> Unit) {
    val colorScheme = colorScheme()
    val t = Typography().MCFont().ModifyAll {
        it.copy(color = colorScheme.onBackground)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        typography = t
    )
}

@Composable
private fun colorScheme() = when(Ascendium.settings.themeState) {
    "Dark" -> darkColorScheme().setButtonColors()
    "Light" -> lightColorScheme().setButtonColors()
    "Minecraft" -> mcColorScheme
    "Diamond" -> diamondColorScheme
    else -> throw IllegalStateException()
}

private fun ColorScheme.setButtonColors() = this.copy(primary = primaryContainer, onPrimary = onPrimaryContainer)

private val mcColorScheme = ColorScheme(
    primary = Color(0xFF6A9B54),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF456B37),
    onPrimaryContainer = Color(0xFFB3E0A3),
    inversePrimary = Color(0xFF6A9B54),
    secondary = Color(0xFF8D9296),
    onSecondary = Color(0xFF1E2122),
    secondaryContainer = Color(0xFF42474A),
    onSecondaryContainer = Color(0xFFDCE2E6),
    tertiary = Color(0xFF68D1E3),
    onTertiary = Color(0xFF00252A),
    tertiaryContainer = Color(0xFF004D58),
    onTertiaryContainer = Color(0xFFB9F2FF),
    background = Color(0xFF1A1C19),
    onBackground = Color(0xFFE2E3DE),
    surface = Color(0xFF1A1C19),
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = Color(0xFF414940),
    onSurfaceVariant = Color(0xFFC1C9BE),
    surfaceTint = Color(0xFF6A9B54),
    inverseSurface = Color(0xFFE2E3DE),
    inverseOnSurface = Color(0xFF1A1C19),
    error = Color(0xFFC53434),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7A2020),
    onErrorContainer = Color(0xFFF9DADA),
    outline = Color(0xFF8B9389),
    outlineVariant = Color(0xFF414940),
    scrim = Color(0x99000000),
    surfaceDim = Color(0xFF121411),
    surfaceBright = Color(0xFF383A36),
    surfaceContainerLowest = Color(0xFF151714),
    surfaceContainerLow = Color(0xFF1A1C19),
    surfaceContainer = Color(0xFF1E201D),
    surfaceContainerHigh = Color(0xFF282B27),
    surfaceContainerHighest = Color(0xFF333632),
)

private val diamondColorScheme = ColorScheme(
    primary = Color(0xFF6C99FF),
    onPrimary = Color(0xFF001D53),
    primaryContainer = Color(0xFF2E468A),
    onPrimaryContainer = Color(0xFFD9E2FF),
    inversePrimary = Color(0xFF4C61A1),
    secondary = Color(0xFF8A9296),
    onSecondary = Color(0xFF1E2122),
    secondaryContainer = Color(0xFF42474A),
    onSecondaryContainer = Color(0xFFDCE2E6),
    tertiary = Color(0xFFDDB0FF),
    onTertiary = Color(0xFF432258),
    tertiaryContainer = Color(0xFF5C3A70),
    onTertiaryContainer = Color(0xFFF0D9FF),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF42474C),
    onSurfaceVariant = Color(0xFFC2C7CD),
    surfaceTint = Color(0xFF6C99FF),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF1A1C1E),
    error = Color(0xFFC53434),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7A2020),
    onErrorContainer = Color(0xFFF9DADA),
    outline = Color(0xFF8C9198),
    outlineVariant = Color(0xFF42474C),
    scrim = Color(0x99000000),
    surfaceDim = Color(0xFF111415),
    surfaceBright = Color(0xFF373A3C),
    surfaceContainerLowest = Color(0xFF151718),
    surfaceContainerLow = Color(0xFF1A1C1E),
    surfaceContainer = Color(0xFF1E2022),
    surfaceContainerHigh = Color(0xFF282A2C),
    surfaceContainerHighest = Color(0xFF333537),
)