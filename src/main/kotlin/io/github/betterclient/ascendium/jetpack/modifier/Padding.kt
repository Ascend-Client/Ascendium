package io.github.betterclient.ascendium.jetpack.modifier

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.HTMLModifierElement
import io.github.betterclient.ascendium.jetpack.require

fun HTMLModifier.padding(all: Float) =
    this.padding(all, all, all, all)

fun HTMLModifier.padding(all: Int) =
    this.padding(all = all.toFloat())

fun HTMLModifier.padding(top: Float = 0f, right: Float = 0f, bottom: Float = 0f, left: Float = 0f) =
    this then PaddingElement(top, right, bottom, left)

fun HTMLModifier.padding(top: Int = 0, right: Int = 0, bottom: Int = 0, left: Int = 0) =
    this then PaddingElement(top.toFloat(), right.toFloat(), bottom.toFloat(), left.toFloat())

class PaddingElement(top: Float, right: Float, bottom: Float, left: Float) : HTMLModifierElement {
    override val css = "padding: ${
        top.require(from = 0f, fromInclusive = true)
    }px ${
        right.require(from = 0f, fromInclusive = true)
    }px ${
        bottom.require(from = 0f, fromInclusive = true)
    }px ${
        left.require(from = 0f, fromInclusive = true)
    }px"
}