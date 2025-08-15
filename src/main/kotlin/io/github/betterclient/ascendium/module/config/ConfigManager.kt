package io.github.betterclient.ascendium.module.config

import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.module.ModManager
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

object ConfigManager {
    var activeConfig = "main"
    init {
        //generate directory ".ascendium" if it does not exist
        val dir = File(".ascendium")
        if (!dir.exists()) {
            dir.mkdirs()

            val configFile = File(dir, "activeConfig.json")
            configFile.createNewFile()
            configFile.writeText("""{"name": "main"}""")

            val configDir = File(dir, "main")
            configDir.mkdirs()
        }

        val configFile = File(dir, "activeConfig.json")
        val activeConfig = Json.decodeFromString<ActiveConfigFile>(configFile.readText())

        loadConfig(activeConfig.name)
    }

    fun loadConfig(name: String) {
        val configDir = File(".ascendium", name)

        for (module in ModManager.modules) {
            val modConfigFile = File(configDir, "${module.name}.json")
            if (!modConfigFile.exists()) continue
            val modConfig = Json.decodeFromString<ConfigModule>(modConfigFile.readText())

            if (module.enabled != modConfig.enabled) {
                module.toggle()
            }
            for (setting in modConfig.settings) {
                module
                    .settings
                    .find { setting.name == it.name }
                    ?.let {
                        if (it::class == setting::class) {
                            it.setFromString(setting)
                        }
                    }
            }

            if (module is HUDModule) {
                val xSetting = modConfig.settings.find { it.name == "x" } as? NumberSetting
                val ySetting = modConfig.settings.find { it.name == "y" } as? NumberSetting
                if (xSetting != null && ySetting != null) {
                    module.x = xSetting.value.toInt()
                    module.y = ySetting.value.toInt()
                }
            }
        }

        val clientConfig = File(configDir, "Ascendium.json")
        if (clientConfig.exists()) {
            val modConfig = Json.decodeFromString<ConfigModule>(clientConfig.readText())
            for (setting in modConfig.settings) {
                Ascendium.settings
                    .settings
                    .find { setting.name == it.name }
                    ?.let {
                        if (it::class == setting::class) {
                            it.setFromString(setting)
                        }
                    }
            }
        }

        activeConfig = name
        val activeConfigFile = File(".ascendium", "activeConfig.json")
        activeConfigFile.writeText(Json.encodeToString(ActiveConfigFile(name)))
    }

    fun saveConfig() {
        val configDir = File(".ascendium", activeConfig)
        if (!configDir.exists()) configDir.mkdirs()

        for (module in ModManager.modules) {
            val modConfigFile = File(configDir, "${module.name}.json")
            val modConfig = ConfigModule(
                enabled = module.enabled,
                settings = module.settings
            )
            if (module is HUDModule) {
                modConfig.settings = modConfig.settings.toMutableList().also {
                    it.add(NumberSetting("x", module.x.toDouble()))
                    it.add(NumberSetting("y", module.y.toDouble()))
                }
            }
            modConfigFile.writeText(Json.encodeToString(modConfig))
        }

        val clientConfigFile = File(configDir, "Ascendium.json")
        val cfg = ConfigModule(true, Ascendium.settings.settings)
        clientConfigFile.writeText(Json.encodeToString(cfg))
    }
}

@Serializable
class ActiveConfigFile(val name: String)

@Serializable
class ConfigModule(val enabled: Boolean, var settings: List<Setting>)