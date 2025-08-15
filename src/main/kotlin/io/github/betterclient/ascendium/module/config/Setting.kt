package io.github.betterclient.ascendium.module.config

import kotlinx.serialization.Serializable

@Serializable
sealed class Setting() {
    abstract val name: String

    abstract fun setFromString(json: Setting)
    abstract fun reset()
}

@Serializable
class BooleanSetting(
    override val name: String,
    private val _value: Boolean = false
) : Setting() {
    var value = _value
    fun toggle() {
        value = !value
    }

    override fun setFromString(json: Setting) {
        val out = json as BooleanSetting //if type doesn't match, blame the config manager, not us, this shouldn't end up here if its the wrong type
        value = out.value
    }

    override fun reset() {
        value = _value
    }
}

@Serializable
class NumberSetting(
    override val name: String,
    private val _value: Double = 0.0,
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
) : Setting() {
    var value = _value
    override fun setFromString(json: Setting) {
        val out = json as NumberSetting
        value = out.value.coerceIn(min, max)
    }

    override fun reset() {
        value = _value
    }
}

@Serializable
class StringSetting(
    override val name: String,
    private val _value: String = "",
    val maxLength: Int = 256,
) : Setting() {
    var value = _value
    override fun setFromString(json: Setting) {
        val out = json as StringSetting
        value = out.value.take(maxLength)
    }

    override fun reset() {
        value = _value
    }
}

@Serializable
class DropdownSetting(
    override val name: String,
    private val _value: String = "",
    val options: List<String> = emptyList(),
) : Setting() {
    var value = _value
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

    override fun reset() {
        value = _value
    }
}

@Serializable
class ColorSetting(
    override val name: String,
    private val _value: Int = -1,
) : Setting() {
    var value = _value
    override fun setFromString(json: Setting) {
        val out = json as ColorSetting
        value = out.value
    }

    override fun reset() {
        value = _value
    }
}