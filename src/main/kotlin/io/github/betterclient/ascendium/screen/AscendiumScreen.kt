package io.github.betterclient.ascendium.screen

import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.BridgeScreen

class AscendiumScreen : BridgeScreen() {
    override fun render(renderer: BridgeRenderer, mouseX: Int, mouseY: Int) {
        renderer.drawText("Hello world!", width / 2 - (renderer.getTextWidth("Hello world!") * 2) / 2, height / 2 - 10, -1, 2.0f)
    }
}