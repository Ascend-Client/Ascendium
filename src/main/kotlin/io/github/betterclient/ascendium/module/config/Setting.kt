package io.github.betterclient.ascendium.module.config

import io.github.betterclient.ascendium.module.Module
import kotlinx.serialization.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Serializable
sealed class Setting() {
    abstract val name: String

    abstract fun setFromString(json: Setting)
}

@Serializable
class BooleanSetting(
    override val name: String,
    var value: Boolean = false
) : Setting() {
    fun toggle() {
        value = !value
    }

    override fun setFromString(json: Setting) {
        val out = json as BooleanSetting //if type doesn't match, blame the config manager, not us, this shouldn't end up here if its the wrong type
        value = out.value
    }

    fun delegate(module: Module) = module.settings.add(this).let { ::value } //dirty hack
}

@Serializable
class NumberSetting(
    override val name: String,
    var value: Double = 0.0,
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
) : Setting() {
    override fun setFromString(json: Setting) {
        val out = json as NumberSetting
        value = out.value.coerceIn(min, max)
    }

    fun delegate(module: Module) = module.settings.add(this).let { ::value } //dirty hack
}

@Serializable
class StringSetting(
    override val name: String,
    var value: String = "",
    val maxLength: Int = 256,
) : Setting() {
    override fun setFromString(json: Setting) {
        val out = json as StringSetting
        value = out.value.take(maxLength)
    }

    fun delegate(module: Module) = module.settings.add(this).let { ::value }
}

@Serializable
class DropdownSetting(
    override val name: String,
    var value: String = "",
    val options: List<String> = emptyList(),
) : Setting() {
    fun set(newValue: String) {
        value = if (options.contains(newValue)) {
            newValue
        } else {
            options.first()
        }
    }

    override fun setFromString(json: Setting) {
        val out = json as DropdownSetting
        set(out.value)
    }

    fun delegate(module: Module) = module.settings.add(this).let { ::value }
}

@Serializable
class ColorSetting(
    override val name: String,
    var value: Int = -1,
) : Setting() {
    override fun setFromString(json: Setting) {
        val out = json as ColorSetting
        value = out.value
    }

    fun delegate(module: Module) = module.settings.add(this).let { ::value } //dirty hack
}