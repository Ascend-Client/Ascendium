package io.github.betterclient.ascendium.module.config

import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.Logger
import io.github.betterclient.ascendium.module.ComposableHUDModule
import io.github.betterclient.ascendium.module.ModManager
import kotlinx.serialization.Serializable
import java.io.File

object ConfigManager {
    var activeConfig = "main"
    val json = kotlinx.serialization.json.Json { encodeDefaults = true }

    init {
        //generate directory ".ascendium" if it does not exist
        val dir = File(".ascendium")
        val dir2 = File(dir, "configs")
        if (!dir.exists() || !dir2.exists()) {
            dir.mkdirs()
            dir2.mkdirs()

            val configFile = File(dir2, "activeConfig.json")
            configFile.createNewFile()
            configFile.writeText("""{"name": "main"}""")

            val configDir = File(dir2, "main")
            configDir.mkdirs()
        }

        val configFile = File(dir2, "activeConfig.json")
        val activeConfig = json.decodeFromString<ActiveConfigFile>(configFile.readText())

        loadConfig(activeConfig.name)
    }

    fun loadConfig(name: String) {
        val configDir = File(".ascendium/configs", name)

        for (module in ModManager.modules) {
            val modConfigFile = File(configDir, "${module.name}.json")
            if (!modConfigFile.exists()) continue
            val modConfig: ConfigModule
            try {
                 modConfig = json.decodeFromString(modConfigFile.readText())
            } catch (_: Exception) {
                Logger.info("Corrupted mod ${module.name}")
                continue
            }

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

            if (module is ComposableHUDModule) {
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
            val modConfig: ConfigModule
            try {
                modConfig = json.decodeFromString(clientConfig.readText())
            } catch (_: Exception) {
                Logger.info("Client config corrupted")
                return
            }
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
        val activeConfigFile = File(".ascendium/configs", "activeConfig.json")
        activeConfigFile.writeText(json.encodeToString(ActiveConfigFile(name)))
    }

    fun saveConfig() {
        val configDir = File(".ascendium/configs", activeConfig)
        if (!configDir.exists()) configDir.mkdirs()

        for (module in ModManager.modules) {
            val modConfigFile = File(configDir, "${module.name}.json")
            val modConfig = ConfigModule(
                enabled = module.enabled,
                settings = module.settings
            )
            if (module is ComposableHUDModule) {
                modConfig.settings = modConfig.settings.toMutableList().also {
                    it.add(NumberSetting("x", module.x.toDouble()))
                    it.add(NumberSetting("y", module.y.toDouble()))
                }
            }
            modConfigFile.writeText(json.encodeToString(modConfig))
        }

        val clientConfigFile = File(configDir, "Ascendium.json")
        val cfg = ConfigModule(true, Ascendium.settings.settings)
        clientConfigFile.writeText(json.encodeToString(cfg))
    }
}

@Serializable
class ActiveConfigFile(val name: String)

@Serializable
class ConfigModule(val enabled: Boolean, var settings: List<Setting>)