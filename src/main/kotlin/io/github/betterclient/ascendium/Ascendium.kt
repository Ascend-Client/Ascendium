package io.github.betterclient.ascendium

import dev.datlag.kcef.KCEF
import dev.datlag.kcef.KCEFClient
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.module.config.DropdownSetting
import io.github.betterclient.ascendium.module.config.NumberSetting
import io.github.betterclient.ascendium.ui.chrome.OffscreenBrowser
import io.github.betterclient.ascendium.ui.move.MoveModuleUI
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.File
import javax.swing.JOptionPane
import kotlin.system.exitProcess

object Ascendium {
    val settings = ClientSettings()

    fun start() {
        Logger.info("Starting!")
        ConfigManager

        Bridge.client.gameOptions.addKeybinding(
            defaultKey = 344,
            name = "Open GUI",
            category = "Ascendium"
        ).onPressed {
            Bridge.client.openScreen(MoveModuleUI(ModManager.getHUDModules()))
        }

        //commenting this will also disable easter egg
        //OffscreenBrowser.init()
    }
}

object Logger {
    internal val logger = LoggerFactory.getLogger("Ascendium")

    fun info(s: String) = logger.info(s)
}

class ClientSettings {
    private val _bo = NumberSetting("Background opacity", 0.7, 0.1, 1.0)
    val backgroundOpacity by _bo::value

    private val _t = DropdownSetting("Theme", "Dark", listOf("Dark", "Light"))
    val theme by _t::value

    val settings = listOf(_bo, _t)
}