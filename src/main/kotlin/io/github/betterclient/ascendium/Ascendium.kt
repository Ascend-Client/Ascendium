package io.github.betterclient.ascendium

import androidx.compose.runtime.getValue
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.module.config.BooleanSetting
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.module.config.DropdownSetting
import io.github.betterclient.ascendium.module.config.NumberSetting
import io.github.betterclient.ascendium.ui.move.MoveModuleUI
import org.slf4j.LoggerFactory

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
    val backgroundOpacityState by _bo.state

    private val _t = DropdownSetting("Theme", "Minecraft", listOf("Minecraft", "Diamond", "Dark", "Light"))
    val themeState by _t.state

    private val _mf = BooleanSetting("Use minecraft font in UI's", false)
    val mcFontState: Boolean by _mf.state

    val settings = listOf(_t, _mf, _bo)
}