package io.github.betterclient.ascendium.module.config

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Setting() {
    abstract val name: String

    abstract fun setFromString(json: Setting)
    protected abstract fun _reset()

    fun reset() {
        _reset()
        ConfigManager.saveConfig()
    }
}

@Serializable
class BooleanSetting(
    override val name: String,
    private val _value: Boolean = false
) : Setting() {
    @Transient val state: MutableState<Boolean> = mutableStateOf(_value)

    var value
        set(value) {
            state.value = value
        }
        get() = state.value

    override fun setFromString(json: Setting) {
        val out = json as BooleanSetting //if type doesn't match, blame the config manager, not us, this shouldn't end up here if its the wrong type
        value = out.value
    }

    override fun _reset() {
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
    @Transient val state: MutableState<Double> = mutableStateOf(_value)

    var value
        set(value) {
            state.value = value
        }
        get() = state.value

    override fun setFromString(json: Setting) {
        val out = json as NumberSetting
        value = out.value.coerceIn(min, max)
    }

    override fun _reset() {
        value = _value
    }
}

@Serializable
class StringSetting(
    override val name: String,
    private val _value: String = ""
) : Setting() {
    @Transient val state: MutableState<String> = mutableStateOf(_value)

    var value
        set(value) {
            state.value = value
        }
        get() = state.value

    override fun setFromString(json: Setting) {
        val out = json as StringSetting
        value = out.value
    }

    override fun _reset() {
        value = _value
    }
}

@Serializable
class DropdownSetting(
    override val name: String,
    private val _value: String = "",
    val options: List<String> = emptyList(),
) : Setting() {
    @Transient val state: MutableState<String> = mutableStateOf(_value)

    var value
        set(value) {
            state.value = value
        }
        get() = state.value

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

    override fun _reset() {
        value = _value
    }
}

@Serializable
class ColorSetting(
    override val name: String,
    private val _value: Int = -1,
) : Setting() {

    @Transient val state: MutableState<Int> = mutableStateOf(_value)

    var value
        set(value) {
            state.value = value
        }
        get() = state.value

    override fun setFromString(json: Setting) {
        val out = json as ColorSetting
        value = out.value
    }

    override fun _reset() {
        value = _value
    }
}