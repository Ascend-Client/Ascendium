package io.github.betterclient.ascendium.module.config

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

            module.enabled = modConfig.enabled
            for (setting in modConfig.settings) {
                module
                    .settings
                    .find { setting.name == it.name }
                    ?.setFromString(Json.encodeToString(setting))
            }

            if (module is HUDModule) {
                val xSetting = module.settings.find { it.name == "x" } as? NumberSetting
                val ySetting = module.settings.find { it.name == "y" } as? NumberSetting
                if (xSetting != null && ySetting != null) {
                    module.x = xSetting.value.toInt()
                    module.y = ySetting.value.toInt()
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
    }
}

@Serializable
class ActiveConfigFile(val name: String)

@Serializable
class ConfigModule(val enabled: Boolean, var settings: List<Setting>)