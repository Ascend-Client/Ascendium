package io.github.betterclient.ascendium.module

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.betterclient.ascendium.event.eventBus
import io.github.betterclient.ascendium.module.config.*

open class Module(val name: String, val description: String) {
    var enabled: Boolean by mutableStateOf(false)
    val settings: MutableList<Setting> = mutableStateListOf()

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

    fun boolean(name: String, value: Boolean, condition: () -> Boolean = { true }) =
        BooleanSetting(name, value, condition).apply { this@Module.settings.add(this) }.state

    fun number(name: String, value: Double, min: Double = Double.NEGATIVE_INFINITY, max: Double = Double.POSITIVE_INFINITY, condition: () -> Boolean = { true }) =
        NumberSetting(name, value, min, max, condition).apply { this@Module.settings.add(this) }.state

    fun string(name: String, value: String, condition: () -> Boolean = { true }) =
        StringSetting(name, value, condition).apply { this@Module.settings.add(this) }.state

    fun dropdown(name: String, value: String, vararg options: String, condition: () -> Boolean = { true }) =
        DropdownSetting(name, value, options.toList().let { it.toMutableList().apply { this.addFirst(value) } }, condition).apply { this@Module.settings.add(this) }.state

    fun color(name: String, value: Int, condition: () -> Boolean = { true }) =
        ColorSetting(name, value, condition).apply { this@Module.settings.add(this) }.state

    fun info(str: String, condition: () -> Boolean = { true }) = InfoSetting(str, condition = condition).apply { this@Module.settings.add(this) }

    infix fun `is`(@Suppress("UNUSED_PARAMETER") state: enabled) = this.enabled
    infix fun `is`(@Suppress("UNUSED_PARAMETER") state: disabled) = !this.enabled
}
object enabled
object disabled