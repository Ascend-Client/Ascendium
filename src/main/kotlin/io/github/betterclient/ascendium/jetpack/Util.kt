package io.github.betterclient.ascendium.jetpack

fun Float.require(
    from: Float = Float.MIN_VALUE,
    to: Float = Float.MAX_VALUE,
    fromInclusive: Boolean = true,
    toInclusive: Boolean = true
): Float {
    val lowerOk = if (fromInclusive) this >= from else this > from
    val upperOk = if (toInclusive) this <= to else this < to

    if (!lowerOk || !upperOk) {
        throw IllegalArgumentException(
            "Value $this not in range ${if (fromInclusive) "[" else "("}$from, $to${if (toInclusive) "]" else ")"}"
        )
    }
    return this
}

//base classes for css
enum class LayoutComponent { BOX, ROW, COLUMN }