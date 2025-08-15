package io.github.betterclient.ascendium

import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.ui.mods.ModsUI
import org.slf4j.LoggerFactory

object Ascendium {
    fun start() {
        Logger.info("Starting!")
        ConfigManager

        Bridge.client.gameOptions.addKeybinding(
            defaultKey = 344,
            name = "Open GUI",
            category = "Ascendium"
        ).onPressed {
            Bridge.client.openScreen(ModsUI())
        }
    }
}

object Logger {
    internal val logger = LoggerFactory.getLogger("Ascendium")

    fun info(s: String) = logger.info(s)
}