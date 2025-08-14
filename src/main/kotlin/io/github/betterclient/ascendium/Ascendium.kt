package io.github.betterclient.ascendium

import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.event.eventBus
import io.github.betterclient.ascendium.screen.AscendiumScreen
import org.jetbrains.skia.Paint
import org.slf4j.LoggerFactory

object Ascendium {
    fun start() {
        Logger.info("Starting!")

        Bridge.client.gameOptions.addKeybinding(
            defaultKey = 344,
            name = "Open GUI",
            category = "Ascendium"
        ).onPressed {
            Bridge.client.openScreen(
                AscendiumScreen()
            )
        }

        eventBus.subscribe()
    }

    @EventTarget
    fun onRender(renderHudEvent: RenderHudEvent) {
        SkiaRenderer.init()
        SkiaRenderer.withSkia { canvas ->
            canvas.drawCircle(50f, 50f, 25f, Paint().apply { color = -1 })
        }
    }
}

object Logger {
    internal val logger = LoggerFactory.getLogger("Ascendium")

    fun info(s: String) = logger.info(s)
}