package io.github.betterclient.ascendium.module.config

import kotlinx.serialization.Serializable

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
}