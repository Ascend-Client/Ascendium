package io.github.betterclient.ascendium.jetpack.modifier

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.HTMLModifierElement
import io.github.betterclient.ascendium.jetpack.require

fun HTMLModifier.borderRadius(all: Float) =
    this.borderRadius(all, all, all, all)

fun HTMLModifier.borderRadius(all: Int) =
    this.borderRadius(all.toFloat())

fun HTMLModifier.borderRadius(topStart: Float = 0f, topEnd: Float = 0f, bottomEnd: Float = 0f, bottomStart: Float = 0f) =
    this then BorderRadiusElement(topStart, topEnd, bottomEnd, bottomStart)

fun HTMLModifier.borderRadius(topStart: Int = 0, topEnd: Int = 0, bottomEnd: Int = 0, bottomStart: Int = 0) =
    this then BorderRadiusElement(topStart.toFloat(), topEnd.toFloat(), bottomEnd.toFloat(), bottomStart.toFloat())

class BorderRadiusElement(topStart: Float, topEnd: Float, bottomEnd: Float, bottomStart: Float) : HTMLModifierElement {
    override val css = "border-radius: ${
        topStart.require(from = 0f, fromInclusive = true)
    }px ${
        topEnd.require(from = 0f, fromInclusive = true)
    }px ${
        bottomEnd.require(from = 0f, fromInclusive = true)
    }px ${
        bottomStart.require(from = 0f, fromInclusive = true)
    }px"
}