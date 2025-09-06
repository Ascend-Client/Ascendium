package io.github.betterclient.ascendium.jetpack.element

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.JetpackContext
import io.github.betterclient.ascendium.jetpack.aware
import io.github.betterclient.ascendium.jetpack.mappings
import io.github.betterclient.ascendium.jetpack.remake
import io.github.betterclient.ascendium.jetpack.toStyleString
import kotlinx.html.button
import kotlinx.html.style
import kotlin.random.Random

inline fun JetpackContext.HTMLButton(
    modifier: HTMLModifier = HTMLModifier,
    crossinline content: JetpackContext.() -> Unit,
    noinline onClick: () -> Unit
) {
    val finalModifier = aware(modifier)
    button {
        style = finalModifier.elements.toStyleString()

        val clickHandler = "button_clicked_${Random.nextLong(0, Long.MAX_VALUE)}"
        mappings[clickHandler] = onClick
        attributes["onclick"] = "window.cefQuery({ request: '$clickHandler' })"

        remake(this@HTMLButton).content()
    }
}