package io.github.betterclient.ascendium.jetpack

import io.github.betterclient.ascendium.jetpack.element.BoxAlignModifierElement
import io.github.betterclient.ascendium.jetpack.element.HTMLBoxScope

open class HTMLModifier(
    val elements: List<HTMLModifierElement> = listOf()
) {
    companion object : HTMLModifier() {
        override fun then(other: HTMLModifier): HTMLModifier = other
    }

    open infix fun then(other: HTMLModifier): HTMLModifier {
        return HTMLModifier(this.elements + other.elements)
    }

    infix fun then(other: HTMLModifierElement): HTMLModifier {
        return HTMLModifier(this.elements + other)
    }
}

interface HTMLModifierElement {
    val css: String
}

fun JetpackContext.aware(modifier: HTMLModifier): HTMLModifier {
    var finalModifier = modifier
    if (this is HTMLBoxScope) {
        val explicitAlignment = modifier.elements.find { it is BoxAlignModifierElement } as? BoxAlignModifierElement

        if (explicitAlignment != null) {
            val alignmentElements = explicitAlignment.alignment.getModifierElements(LayoutComponent.BOX)
            finalModifier = HTMLModifier(alignmentElements + modifier.elements)
        } else {
            val alignmentElements = this.contentAlignment.getModifierElements(LayoutComponent.BOX)
            finalModifier = HTMLModifier(alignmentElements + modifier.elements)
        }
    }
    return finalModifier
}

fun List<HTMLModifierElement>.toStyleString(): String {
    val allCss = this.map { it.css }

    val transforms = allCss
        .filter { it.startsWith("transform:") }
        .map { it.removePrefix("transform:").trim() }
    val otherCss = allCss.filterNot { it.startsWith("transform:") }

    val styleParts = otherCss.toMutableList()
    if (transforms.isNotEmpty()) {
        styleParts.add("transform: ${transforms.joinToString(" ")}")
    }
    return styleParts.joinToString("; ")
}