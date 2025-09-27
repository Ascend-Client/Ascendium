package io.github.betterclient.ascendium

import androidx.compose.runtime.getValue
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.bridge.requireOffscreen
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.module.config.BooleanSetting
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.module.config.DropdownSetting
import io.github.betterclient.ascendium.module.config.NumberSetting
import io.github.betterclient.ascendium.ui.chrome.ChromiumDownloader
import io.github.betterclient.ascendium.ui.move.MoveModuleUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.slf4j.LoggerFactory

object Ascendium {
    val settings = ClientSettings()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun start() {
        Logger.info("Starting!")
        ConfigManager

        minecraft.gameOptions.addKeybinding(
            defaultKey = 344,
            name = "Open GUI",
            category = "Ascendium"
        ).onPressed {
            minecraft.openScreen(MoveModuleUI(ModManager.getHUDModules()))
        }

        //commenting this will also disable easter egg
        ChromiumDownloader.download()

        //colorpicker crash
        Dispatchers.setMain(Dispatchers.Default)

        if (requireOffscreen) {
            settings._ui.options.remove("Compose")
            //this version requires offscreen(compatibility)
            if (settings._ui.value == "Compose") {
                settings._ui.set("Offscreen")
            }
        }
    }
}

object Logger {
    internal val logger = LoggerFactory.getLogger("Ascendium")

    fun info(s: String) = logger.info(s)
    fun error(s: String) = logger.error(s)
}

class ClientSettings {
    private val _bo = NumberSetting("Background opacity", 0.7, 0.1, 1.0)
    val backgroundOpacityState by _bo.state

    private val _t = DropdownSetting("Theme", "Minecraft", mutableListOf("Minecraft", "Diamond", "Dark", "Light"))
    val themeState by _t.state

    private val _mf = BooleanSetting("Use minecraft font in UI's", false)
    val mcFontState by _mf.state

    val _ui = DropdownSetting("UI Backend (changed on restart)", "Compose", mutableListOf("Compose", "Offscreen", "Offscreen (compatibility)"))
    val uiBackend by _ui.state

    val settings = mutableListOf(_t, _mf, _bo, _ui)
}