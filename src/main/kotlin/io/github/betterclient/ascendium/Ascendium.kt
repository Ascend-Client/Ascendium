package io.github.betterclient.ascendium

import io.github.betterclient.ascendium.screen.AscendiumScreen
import org.slf4j.LoggerFactory

object Ascendium {
    fun start() {
        Logger.info("Starting!")

        Bridge.client.gameOptions.addKeybinding(
            defaultKey = 344,
            name = "Open GUI",
            category = "Ascendium"
        ).onPressed {
            Bridge.client.openScreen(AscendiumScreen)
        }
    }
}

object Logger {
    internal val logger = LoggerFactory.getLogger("Ascendium")

    fun info(s: String) = logger.info(s)
}