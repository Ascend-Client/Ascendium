package io.github.betterclient.ascendium.jetpack.element

import io.github.betterclient.ascendium.jetpack.*
import kotlinx.html.p
import kotlinx.html.style
import java.awt.Color

fun JetpackContext.HTMLText(
    text: String,
    modifier: HTMLModifier = HTMLModifier,
    color: Color = Color.BLACK,
    fontSize: Float = 16f,
    fontWeight: Int = 400
) {
    val finalModifier = aware(modifier)
    p {
        style = (finalModifier.elements + listOf(
            RawCssElement("color: rgba(${color.red}, ${color.green}, ${color.blue}, ${color.alpha / 255f})"), //color
            RawCssElement("font-size: ${fontSize}px"), //size
            RawCssElement("font-weight: $fontWeight") //weight
        )).toStyleString()
        +text
    }
}