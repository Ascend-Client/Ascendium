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

inline fun JetpackContext.HTMLColumn(
    modifier: HTMLModifier = HTMLModifier,
    verticalArrangement: HTMLArrangement.Vertical = HTMLArrangement.Top,
    horizontalAlignment: HTMLAlignment.Horizontal = HTMLAlignment.Start,
    crossinline block: JetpackContext.() -> Unit
) {
    val finalModifier = aware(modifier)
    div {
        val allElements = listOf(
            RawCssElement("display: flex"),
            RawCssElement("flex-direction: column")
        ) + verticalArrangement.getModifierElement() +
                horizontalAlignment.getModifierElements(LayoutComponent.COLUMN) +
                finalModifier.elements

        style = allElements.toStyleString()

        remake(this@HTMLColumn).block()
    }
}