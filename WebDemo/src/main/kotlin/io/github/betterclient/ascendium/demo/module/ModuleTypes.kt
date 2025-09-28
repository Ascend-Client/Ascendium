package io.github.betterclient.ascendium.demo.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

open class Module(val name: String, val description: String, val previewHeight: Int, val hasBackground: Boolean = true) {
    var enabled: Boolean by mutableStateOf(false)
    val settings: MutableList<Setting> = mutableStateListOf()
    open val renderBackground = true
    val textColor by color("Text Color", -1)
    val backgroundColor = ColorSetting("Background Color", 0x51000000)

    open fun toggle() {
        enabled = !enabled
        if (enabled) {
            onEnable()
        } else {
            onDisable()
        }
    }

    init {
        if (hasBackground) {
            settings.add(backgroundColor)
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}

    @Composable
    open fun Render() {}

    @Composable
    fun RenderComposable() {
        val bg by backgroundColor.state
        Box {
            Box(
                Modifier
                    .then(
                        if (hasBackground && renderBackground) {
                            Modifier
                                .background(Color(bg), RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                        }
                    )
                    .padding(4.dp)
            ) {
                Render()
            }
        }
    }

    fun boolean(name: String, value: Boolean, condition: () -> Boolean = { true }) =
        BooleanSetting(name, value, condition).apply { this@Module.settings.add(this) }.state

    fun number(name: String, value: Double, min: Double = Double.NEGATIVE_INFINITY, max: Double = Double.POSITIVE_INFINITY, condition: () -> Boolean = { true }) =
        NumberSetting(name, value, min, max, condition).apply { this@Module.settings.add(this) }.state

    fun string(name: String, value: String, condition: () -> Boolean = { true }) =
        StringSetting(name, value, condition).apply { this@Module.settings.add(this) }.state

    fun dropdown(name: String, value: String, vararg options: String, condition: () -> Boolean = { true }) =
        DropdownSetting(name, value, options.toList().let { it.toMutableList().apply { this.add(0, value) } }, condition).apply { this@Module.settings.add(this) }.state

    fun color(name: String, value: Int, condition: () -> Boolean = { true }) =
        ColorSetting(name, value, condition).apply { this@Module.settings.add(this) }.state

    fun info(str: String, condition: () -> Boolean = { true }) = InfoSetting(str, condition = condition).apply { this@Module.settings.add(this) }
}