package io.github.betterclient.ascendium.ui.config

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.*
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import io.github.betterclient.ascendium.ui.utils.detectOutsideClick

@Composable
fun ColorPicker(initial: Color, default: Color, onChange: (Color) -> Unit) {
    val enabled0 = remember { mutableStateOf(false) }
    var enabled by enabled0
    var color by remember { mutableStateOf(initial) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResetButton {
                onChange(default)
                enabled = false
                color = default
            }

            val size by animateDpAsState(if (enabled) 64.dp else 32.dp)

            Box(
                modifier = Modifier
                    .size(size)
                    .background(color, CircleShape)
                    .border(1.dp, AscendiumTheme.colorScheme.outline, CircleShape)
                    .clickable {
                        enabled = !enabled
                    }
            )
        }

        AnimatedVisibility(
            visible = enabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier.padding(top = 8.dp).detectOutsideClick(enabled0) {
                    enabled = false
                },
                shape = AscendiumTheme.shapes.medium,
                shadowElevation = 4.dp,
                color = AscendiumTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, AscendiumTheme.colorScheme.outline)
            ) {
                ColorPickerWindow(
                    initial = color,
                    onChange = { color = it; onChange(it) }
                )
            }
        }
    }
}

@Composable
private fun ColorPickerWindow(initial: Color, onChange: (Color) -> Unit) {
    val controller = rememberColorPickerController()

    LaunchedEffect(initial) {
        controller.selectByColor(initial, false)
    }

    Row(
        Modifier
            .width(700.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .weight(0.8f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColorPickerSliders(controller)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                BrightnessSlider(
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    controller = controller,
                    borderRadius = 8.dp,
                    borderSize = 2.dp,
                    borderColor = AscendiumTheme.colorScheme.outline
                )

                AlphaSlider(
                    modifier = Modifier
                        .weight(1f)
                        .height(35.dp),
                    controller = controller,
                    borderRadius = 8.dp,
                    borderSize = 2.dp,
                    borderColor = AscendiumTheme.colorScheme.outline
                )
            }
        }

        HsvColorPicker(
            modifier = Modifier.weight(0.2f).aspectRatio(1f).padding(end = 16.dp),
            controller = controller,
            onColorChanged = {
                onChange(it.color)
            },
            initialColor = initial
        )
    }
}

@Composable
private fun ColorPickerSliders(controller: ColorPickerController) {
    val color by controller.selectedColor
    val sliders = listOf(
        "Red" to { it: Float -> color.copy(red = it) to color.red },
        "Blue" to { it: Float -> color.copy(blue = it) to color.blue },
        "Green" to { it: Float -> color.copy(green = it) to color.green },
        "Alpha" to { it: Float -> color.copy(alpha = it) to color.alpha },
    )
    val sliderRows = listOf(
        sliders[0] to sliders[1],
        sliders[2] to sliders[3]
    )
    sliderRows.forEach { row ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Slider(row.first, controller)
            Slider(row.second, controller)
        }
    }
}

@Composable
fun RowScope.Slider(slider: Pair<String, (Float) -> Pair<Color, Float>>, controller: ColorPickerController) {
    val (label, fn) = slider
    val (_, value) = fn(0f)

    Text(label)
    OutlinedTextField(
        value = (fn(value).second * 255).toInt().toString(),
        onValueChange = { input ->
            if (input.isBlank()) {
                //auto generate a 0 when all is deleted
                controller.selectByColor(fn(0f).first, true)
            } else if (fn(0f).second == 0f) {
                //remove auto generated 0 when user types
                input.replaceFirst("0", "").toIntOrNull()?.let { controller.selectByColor(fn(it.div(255f).coerceIn(0f, 1f)).first, true) }
            } else {
                input.toIntOrNull()?.let { controller.selectByColor(fn(it.div(255f).coerceIn(0f, 1f)).first, true) }
            }
        },
        modifier = Modifier.weight(1f),
        singleLine = true
    )

    Box(
        Modifier
            .weight(1f)
            .border(2.dp, AscendiumTheme.colorScheme.outline, RoundedCornerShape(2.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Slider(
            value = value,
            onValueChange = { controller.selectByColor(fn(it).first, true) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

val Color.rgb: Int
    get() = java.awt.Color(this.red, this.green, this.blue, this.alpha).rgb