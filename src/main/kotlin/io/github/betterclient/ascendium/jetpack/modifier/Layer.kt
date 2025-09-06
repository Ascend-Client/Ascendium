package io.github.betterclient.ascendium.jetpack.modifier

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.HTMLModifierElement

private data class ZIndexModifier(val index: Int) : HTMLModifierElement {
    override val css = "z-index: $index"
}

fun HTMLModifier.zIndex(index: Int) = this then ZIndexModifier(index)