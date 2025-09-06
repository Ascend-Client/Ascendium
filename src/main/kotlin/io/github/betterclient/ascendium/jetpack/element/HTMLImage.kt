package io.github.betterclient.ascendium.jetpack.element

import io.github.betterclient.ascendium.jetpack.HTMLModifier
import io.github.betterclient.ascendium.jetpack.JetpackContext
import io.github.betterclient.ascendium.jetpack.aware
import io.github.betterclient.ascendium.jetpack.toStyleString
import kotlinx.html.img
import kotlinx.html.style

inline fun JetpackContext.HTMLImage(
    src: String,
    alt: String = "Image",
    modifier: HTMLModifier = HTMLModifier
) {
    img {
        this.src = src
        this.alt = alt
        this.style = this@HTMLImage.aware(modifier).elements.toStyleString()
    }
}