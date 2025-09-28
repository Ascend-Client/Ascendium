package io.github.betterclient.ascendium.demo.module

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Setting {
    abstract val name: String
    abstract val condition: () -> Boolean

    abstract fun setFromSetting(setting: Setting)
    protected abstract fun _reset()

    fun reset() {
        _reset()
    }
}

class BooleanSetting(
    override val name: String,
    private val _defaultValue: Boolean = false,
    override val condition: () -> Boolean = { true }
) : Setting() {
    val state: MutableState<Boolean> = mutableStateOf(_defaultValue)
    var value: Boolean
        get() = state.value
        set(v) {
            state.value = v
        }

    override fun setFromSetting(setting: Setting) {
        val s = setting as BooleanSetting
        value = s.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}

class NumberSetting(
    override val name: String,
    private val _defaultValue: Double = 0.0,
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
    override val condition: () -> Boolean = { true }
) : Setting() {
    val state: MutableState<Double> = mutableStateOf(_defaultValue)
    var value: Double
        get() = state.value
        set(v) {
            state.value = v.coerceIn(min, max)
        }

    override fun setFromSetting(setting: Setting) {
        val s = setting as NumberSetting
        value = s.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}

class StringSetting(
    override val name: String,
    private val _defaultValue: String = "",
    override val condition: () -> Boolean = { true }
) : Setting() {
    val state: MutableState<String> = mutableStateOf(_defaultValue)
    var value: String
        get() = state.value
        set(v) {
            state.value = v
        }

    override fun setFromSetting(setting: Setting) {
        val s = setting as StringSetting
        value = s.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}

class DropdownSetting(
    override val name: String,
    private val _defaultValue: String = "",
    val options: MutableList<String> = mutableListOf(),
    override val condition: () -> Boolean = { true }
) : Setting() {
    val state: MutableState<String> = mutableStateOf(_defaultValue)
    var value: String
        get() = state.value
        set(v) {
            state.value = if (options.isEmpty()) v else options.firstOrNull { it == v } ?: _defaultValue
        }

    fun set(newValue: String) {
        value = newValue
    }

    override fun setFromSetting(setting: Setting) {
        val s = setting as DropdownSetting
        set(s.value)
    }

    override fun _reset() {
        value = _defaultValue
    }
}

class ColorSetting(
    override val name: String,
    val _defaultValue: Int = -1,
    override val condition: () -> Boolean = { true }
) : Setting() {
    val state: MutableState<Int> = mutableStateOf(_defaultValue)
    var value: Int
        get() = state.value
        set(v) {
            state.value = v
        }

    override fun setFromSetting(setting: Setting) {
        val s = setting as ColorSetting
        value = s.value
    }

    override fun _reset() {
        value = _defaultValue
    }
}

class InfoSetting(
    val info: String,
    override val name: String = info,
    override val condition: () -> Boolean = { true }
) : Setting() {
    override fun setFromSetting(setting: Setting) {}
    override fun _reset() {}
}