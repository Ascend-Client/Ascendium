package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.event.eventBus
import io.github.betterclient.ascendium.module.config.*

open class Module(val name: String, val description: String) {
    var enabled: Boolean = false
    val settings: MutableList<Setting> = mutableListOf()

    open fun toggle() {
        enabled = !enabled
        if (enabled) {
            onEnable()
            eventBus.subscribe()
        } else {
            onDisable()
            eventBus.unsubscribe()
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}

    fun boolean(name: String, value: Boolean) =
        BooleanSetting(name, value).apply { this@Module.settings.add(this) }.state

    fun number(name: String, value: Double, min: Double = Double.NEGATIVE_INFINITY, max: Double = Double.POSITIVE_INFINITY) =
        NumberSetting(name, value, min, max).apply { this@Module.settings.add(this) }.state

    fun string(name: String, value: String) =
        StringSetting(name, value).apply { this@Module.settings.add(this) }.state

    fun dropdown(name: String, value: String, vararg options: String) =
        DropdownSetting(name, value, options.toList()).apply { this@Module.settings.add(this) }.state

    fun color(name: String, value: Int) =
        ColorSetting(name, value).apply { this@Module.settings.add(this) }.state

    fun info(str: String) = InfoSetting(str).apply { this@Module.settings.add(this) }

    infix fun `is`(@Suppress("UNUSED_PARAMETER") state: enabled) = this.enabled
    infix fun `is`(@Suppress("UNUSED_PARAMETER") state: disabled) = !this.enabled
}
object enabled
object disabled