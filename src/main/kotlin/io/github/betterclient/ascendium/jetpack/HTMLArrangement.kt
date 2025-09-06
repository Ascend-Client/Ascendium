package io.github.betterclient.ascendium.jetpack

object HTMLArrangement {
    fun interface Horizontal { fun getModifierElement(): HTMLModifierElement }
    fun interface Vertical { fun getModifierElement(): HTMLModifierElement }

    val Start = Horizontal { RawCssElement("justify-content: flex-start") }
    val Center = Horizontal { RawCssElement("justify-content: center") }
    val End = Horizontal { RawCssElement("justify-content: flex-end") }
    val SpaceBetween = Horizontal { RawCssElement("justify-content: space-between") }
    val SpaceAround = Horizontal { RawCssElement("justify-content: space-around") }
    val SpaceEvenly = Horizontal { RawCssElement("justify-content: space-evenly") }

    val Top = Vertical { RawCssElement("justify-content: flex-start") }
    val Center_ = Vertical { RawCssElement("justify-content: center") }
    val Bottom = Vertical { RawCssElement("justify-content: flex-end") }
}