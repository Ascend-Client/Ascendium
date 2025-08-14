package io.github.betterclient.ascendium.module

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
sealed class Setting {
    abstract val name: String

    abstract fun setFromString(json: String)
}

@Serializable
class BooleanSetting(
    override val name: String,
    var value: Boolean = false
) : Setting() {
    fun toggle() {
        value = !value
    }

    override fun setFromString(json: String) {
        val out = Json.decodeFromString<BooleanSetting>(json)
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
    override fun setFromString(json: String) {
        val out = Json.decodeFromString<NumberSetting>(json)
        value = out.value.coerceIn(min, max)
    }
}

@Serializable
class StringSetting(
    override val name: String,
    var value: String = "",
    val maxLength: Int = 256,
) : Setting() {
    override fun setFromString(json: String) {
        val out = Json.decodeFromString<StringSetting>(json)
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

    override fun setFromString(json: String) {
        val out = Json.decodeFromString<DropdownSetting>(json)
        set(out.value)
    }
}

@Serializable
class ColorSetting(
    override val name: String,
    var value: Int = -1,
) : Setting() {
    override fun setFromString(json: String) {
        val out = Json.decodeFromString<ColorSetting>(json)
        value = out.value
    }
}