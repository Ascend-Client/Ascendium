package io.github.betterclient.ascendium.screen

import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.BridgeScreen
import io.github.betterclient.ascendium.compose.SkiaRenderer
import org.jetbrains.skia.Paint
import java.awt.Color

class AscendiumScreen : BridgeScreen() {
    override fun render(renderer: BridgeRenderer, mouseX: Int, mouseY: Int) {
        SkiaRenderer.withSkia {
            it.drawCircle(150f, 150f, 25f, Paint().apply { color = Color.RED.rgb })
        }
        SkiaRenderer.withSkia {
            it.drawCircle(150f, 150f, 12f, Paint().apply { color = -1 })
        }
    }

    override fun init() {
        SkiaRenderer.init()
    }
}