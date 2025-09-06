package io.github.betterclient.ascendium.jetpack

//FUN INTERFACES I LOVE FUN SIGN ME UP
fun interface HTMLAlignment {
    fun getModifierElements(component: LayoutComponent): List<HTMLModifierElement>

    fun interface Horizontal {
        fun getModifierElements(component: LayoutComponent): List<HTMLModifierElement>
        operator fun plus(other: Vertical): HTMLAlignment = CombinedAlignment(this, other)
    }

    fun interface Vertical {
        fun getModifierElements(component: LayoutComponent): List<HTMLModifierElement>
        operator fun plus(other: Horizontal): HTMLAlignment = CombinedAlignment(other, this)
    }

    companion object {
        val Start = Horizontal { component ->
            when (component) {
                LayoutComponent.BOX -> listOf(RawCssElement("left: 0"))
                LayoutComponent.COLUMN -> listOf(RawCssElement("align-items: flex-start"))
                LayoutComponent.ROW -> error("Cannot use Horizontal alignment in an HTMLRow. Use a Vertical alignment.")
            }
        }
        val CenterHorizontally = Horizontal { component ->
            when (component) {
                LayoutComponent.BOX -> listOf(RawCssElement("left: 50%"), RawCssElement("transform: translateX(-50%)"))
                LayoutComponent.COLUMN -> listOf(RawCssElement("align-items: center"))
                LayoutComponent.ROW -> error("Cannot use Horizontal alignment in an HTMLRow. Use a Vertical alignment.")
            }
        }
        val End = Horizontal { component ->
            when (component) {
                LayoutComponent.BOX -> listOf(RawCssElement("right: 0"))
                LayoutComponent.COLUMN -> listOf(RawCssElement("align-items: flex-end"))
                LayoutComponent.ROW -> error("Cannot use Horizontal alignment in an HTMLRow. Use a Vertical alignment.")
            }
        }

        val Top = Vertical { component ->
            when (component) {
                LayoutComponent.BOX -> listOf(RawCssElement("top: 0"))
                LayoutComponent.ROW -> listOf(RawCssElement("align-items: flex-start"))
                LayoutComponent.COLUMN -> error("Cannot use Vertical alignment in an HTMLColumn. Use a Horizontal alignment.")
            }
        }
        val CenterVertically = Vertical { component ->
            when (component) {
                LayoutComponent.BOX -> listOf(RawCssElement("top: 50%"), RawCssElement("transform: translateY(-50%)"))
                LayoutComponent.ROW -> listOf(RawCssElement("align-items: center"))
                LayoutComponent.COLUMN -> error("Cannot use Vertical alignment in an HTMLColumn. Use a Horizontal alignment.")
            }
        }
        val Bottom = Vertical { component ->
            when (component) {
                LayoutComponent.BOX -> listOf(RawCssElement("bottom: 0"))
                LayoutComponent.ROW -> listOf(RawCssElement("align-items: flex-end"))
                LayoutComponent.COLUMN -> error("Cannot use Vertical alignment in an HTMLColumn. Use a Horizontal alignment.")
            }
        }

        val TopStart: HTMLAlignment = Top + Start
        val TopCenter: HTMLAlignment = Top + CenterHorizontally
        val TopEnd: HTMLAlignment = Top + End
        val CenterStart: HTMLAlignment = CenterVertically + Start
        val Center: HTMLAlignment = CenterVertically + CenterHorizontally
        val CenterEnd: HTMLAlignment = CenterVertically + End
        val BottomStart: HTMLAlignment = Bottom + Start
        val BottomCenter: HTMLAlignment = Bottom + CenterHorizontally
        val BottomEnd: HTMLAlignment = Bottom + End
    }
}

private class CombinedAlignment(
    private val h: HTMLAlignment.Horizontal,
    private val v: HTMLAlignment.Vertical
) : HTMLAlignment {
    override fun getModifierElements(component: LayoutComponent): List<HTMLModifierElement> {
        if (component != LayoutComponent.BOX) {
            error("A combined HTMLAlignment (e.g., TopStart) can only be used in an HTMLBox.")
        }
        return listOf(RawCssElement("position: absolute")) +
                h.getModifierElements(component) +
                v.getModifierElements(component)
    }
}

data class RawCssElement(override val css: String) : HTMLModifierElement