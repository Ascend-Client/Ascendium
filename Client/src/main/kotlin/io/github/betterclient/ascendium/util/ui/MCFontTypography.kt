package io.github.betterclient.ascendium.util.ui

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import io.github.betterclient.ascendium.Ascendium

val font = FontFamily(Font(
    "Minecraft",
    Ascendium::class.java.getResourceAsStream("/assets/ascendium/Minecraft.ttf")!!.use { it.readAllBytes() },
    FontWeight.Medium
))

fun TextStyle.MCFont(): TextStyle {
    return this.copy(fontFamily = font, fontSize = this.fontSize * 1.3)
}

fun Typography.MCFont(): Typography {
    return this.ModifyAll { it.MCFont() }
}

inline fun Typography.ModifyAll(textStyleModifier: (TextStyle) -> TextStyle = { it }): Typography {
    return this.copy(
        displayLarge = textStyleModifier(this.displayLarge),
        displayMedium = textStyleModifier(this.displayMedium),
        displaySmall = textStyleModifier(this.displaySmall),
        headlineLarge = textStyleModifier(this.headlineLarge),
        headlineMedium = textStyleModifier(this.headlineMedium),
        headlineSmall = textStyleModifier(this.headlineSmall),
        titleLarge = textStyleModifier(this.titleLarge),
        titleMedium = textStyleModifier(this.titleMedium),
        titleSmall = textStyleModifier(this.titleSmall),
        bodyLarge = textStyleModifier(this.bodyLarge),
        bodyMedium = textStyleModifier(this.bodyMedium),
        bodySmall = textStyleModifier(this.bodySmall),
        labelLarge = textStyleModifier(this.labelLarge),
        labelMedium = textStyleModifier(this.labelMedium),
        labelSmall = textStyleModifier(this.labelSmall),
    )
}