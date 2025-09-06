package io.github.betterclient.ascendium.jetpack.modifier

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.HTMLModifierElement
import java.awt.Color

fun HTMLModifier.background(color: Color) =
    this then BackgroundElement(color)

class BackgroundElement(val color: Color) : HTMLModifierElement {
    override val css = "background-color: rgba(${color.red}, ${color.green}, ${color.blue}, ${color.alpha / 255f})"
}