package io.github.betterclient.ascendium.jetpack.element

import io.github.betterclient.ascendium.jetpack.HTMLAlignment
import io.github.betterclient.ascendium.jetpack.HTMLArrangement
import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.JetpackContext
import io.github.betterclient.ascendium.jetpack.LayoutComponent
import io.github.betterclient.ascendium.jetpack.RawCssElement
import io.github.betterclient.ascendium.jetpack.aware
import io.github.betterclient.ascendium.jetpack.remake
import io.github.betterclient.ascendium.jetpack.toStyleString
import kotlinx.html.div
import kotlinx.html.style

inline fun JetpackContext.HTMLRow(
    modifier: HTMLModifier = HTMLModifier,
    horizontalArrangement: HTMLArrangement.Horizontal = HTMLArrangement.Start,
    verticalAlignment: HTMLAlignment.Vertical = HTMLAlignment.Top,
    crossinline block: JetpackContext.() -> Unit
) {
    val finalModifier = aware(modifier)

    div {
        val allElements = listOf(
            RawCssElement("display: flex"),
            RawCssElement("flex-direction: row")
        ) + horizontalArrangement.getModifierElement() +
                verticalAlignment.getModifierElements(LayoutComponent.ROW) +
                finalModifier.elements
        style = allElements.toStyleString()

        remake(this@HTMLRow).block()
    }
}