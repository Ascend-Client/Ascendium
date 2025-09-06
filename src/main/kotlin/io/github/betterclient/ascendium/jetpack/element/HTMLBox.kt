package io.github.betterclient.ascendium.jetpack.element

import io.github.betterclient.ascendium.jetpack.HTMLAlignment
import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.HTMLModifierElement
import io.github.betterclient.ascendium.jetpack.JetpackContext
import io.github.betterclient.ascendium.jetpack.JetpackServer
import io.github.betterclient.ascendium.jetpack.LayoutComponent
import io.github.betterclient.ascendium.jetpack.RawCssElement
import io.github.betterclient.ascendium.jetpack.aware
import io.github.betterclient.ascendium.jetpack.toStyleString
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.style

class HTMLBoxScope(
    val contentAlignment: HTMLAlignment,
    server: JetpackServer,
    consumer: TagConsumer<*>
) : JetpackContext(server, consumer) {
    fun HTMLModifier.align(alignment: HTMLAlignment): HTMLModifier {
        return this then BoxAlignModifierElement(alignment)
    }
}

class BoxAlignModifierElement(val alignment: HTMLAlignment) : HTMLModifierElement {
    override val css = ""
}

inline fun JetpackContext.HTMLBox(
    modifier: HTMLModifier = HTMLModifier,
    contentAlignment: HTMLAlignment = HTMLAlignment.TopStart,
    crossinline block: HTMLBoxScope.() -> Unit
) {
    val finalModifier = aware(modifier)

    div {
        val allElements = listOf(
            RawCssElement("position: relative")
        ) + finalModifier.elements
        style = allElements.toStyleString()

        HTMLBoxScope(contentAlignment, this@HTMLBox.server, this.consumer).block()
    }
}
