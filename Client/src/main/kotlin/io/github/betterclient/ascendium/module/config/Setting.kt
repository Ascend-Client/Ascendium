package io.github.betterclient.ascendium.module.config

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class Setting() {
    abstract val name: String
    abstract val condition: () -> Boolean

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
    @Transient
    private val _defaultValue: Boolean = false,
    @Transient override val condition: () -> Boolean = { true }
) : Setting() {
    @SerialName("bool_value")
    private var storedValue: Boolean = _defaultValue

    @Transient
    val state: MutableState<Boolean> = mutableStateOf(storedValue)

    var value: Boolean
        get() = state.value
        set(v) {
            state.value = v
            storedValue = v
        }

    init {
        state.value = storedValue
    }

    override fun setFromString(json: Setting) {
        val out = json as BooleanSetting
        value = out.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}


@Serializable
class NumberSetting(
    override val name: String,
    @Transient
    private val _defaultValue: Double = 0.0,
    @Transient
    val min: Double = Double.NEGATIVE_INFINITY,
    @Transient
    val max: Double = Double.POSITIVE_INFINITY,
    @Transient override val condition: () -> Boolean = { true }
) : Setting() {
    @SerialName("number_value")
    private var storedValue: Double = _defaultValue

    @Transient
    val state: MutableState<Double> = mutableStateOf(storedValue)

    var value: Double
        get() = state.value
        set(v) {
            state.value = v.coerceIn(min, max)
            storedValue = state.value
        }

    init {
        state.value = storedValue.coerceIn(min, max)
    }

    override fun setFromString(json: Setting) {
        val out = json as NumberSetting
        value = out.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}

@Serializable
class StringSetting(
    override val name: String,
    @Transient
    private val _defaultValue: String = "",
    @Transient override val condition: () -> Boolean = { true }
) : Setting() {
    @SerialName("string_value")
    private var storedValue: String = _defaultValue
    @Transient
    val state: MutableState<String> = mutableStateOf(storedValue)

    var value: String
        get() = state.value
        set(v) {
            state.value = v
            storedValue = v
        }

    init {
        state.value = storedValue
    }

    override fun setFromString(json: Setting) {
        val out = json as StringSetting
        value = out.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}

@Serializable
class DropdownSetting(
    override val name: String,
    @Transient private val _defaultValue: String = "",
    @Transient val options: List<String> = emptyList(),
    @Transient override val condition: () -> Boolean = { true }
) : Setting() {
    @SerialName("dropdown_value")
    var storedValue: String = _defaultValue

    @Transient
    val state: MutableState<String> = mutableStateOf(_defaultValue)

    var value: String
        get() = state.value
        set(v) {
            if (options.isEmpty()) {
                state.value = v
                storedValue = v
            } else {
                val coerced = if (options.contains(v)) v else options.firstOrNull() ?: _defaultValue
                state.value = coerced
                storedValue = coerced
            }
        }

    fun set(newValue: String) {
        value = newValue
    }

    override fun setFromString(json: Setting) {
        val out = json as DropdownSetting
        set(out.storedValue)
    }

    override fun _reset() {
        value = _defaultValue
    }
}

@Serializable
class ColorSetting(
    override val name: String,
    @Transient
    private val _defaultValue: Int = -1,
    @Transient override val condition: () -> Boolean = { true }
) : Setting() {
    @SerialName("color_value")
    private var storedValue: Int = _defaultValue

    @Transient
    val state: MutableState<Int> = mutableStateOf(storedValue)

    var value: Int
        get() = state.value
        set(v) {
            state.value = v
            storedValue = v
        }

    val defaultValue = _defaultValue

    init {
        state.value = storedValue
    }

    override fun setFromString(json: Setting) {
        val out = json as ColorSetting
        value = out.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}

class InfoSetting(val info: String, override val name: String = info, @Transient override val condition: () -> Boolean = { true }) : Setting() {
    override fun setFromString(json: Setting) { }
    override fun _reset() { }
}