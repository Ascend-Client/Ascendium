package io.github.betterclient.ascendium.jetpack.modifier

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.HTMLModifierElement
import io.github.betterclient.ascendium.jetpack.require

fun HTMLModifier.fillMaxWidth(fraction: Float = 1.0f) =
    this then FillWidthElement(fraction)

fun HTMLModifier.fillMaxHeight(fraction: Float = 1.0f) =
    this then FillHeightElement(fraction)

fun HTMLModifier.fillMaxSize(fraction: Float = 1.0f) =
    this.fillMaxWidth(fraction).fillMaxHeight(fraction)

fun HTMLModifier.width(width: Float) =
    this then WidthElement(width)

fun HTMLModifier.width(width: Int) =
    this then WidthElement(width.toFloat())

fun HTMLModifier.height(height: Float) =
    this then HeightElement(height)

fun HTMLModifier.height(height: Int) =
    this then HeightElement(height.toFloat())

fun HTMLModifier.size(width: Float, height: Float = width) =
    this.width(width).height(height)

fun HTMLModifier.size(width: Int, height: Int = width) =
    this.width(width).height(height)

class WidthElement(amount: Float): HTMLModifierElement {
    override val css = "width: ${amount}px"
}

class HeightElement(amount: Float): HTMLModifierElement {
    override val css = "height: ${amount}px"
}

class FillWidthElement(fraction: Float): HTMLModifierElement {
    override val css = "width: ${fraction.require(0f, 1f, true) * 100}%"
}

class FillHeightElement(fraction: Float): HTMLModifierElement {
    override val css = "height: ${fraction.require(0f, 1f, true) * 100}%"
}