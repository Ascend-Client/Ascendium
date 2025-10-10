package io.github.betterclient.ascendium.ui.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.module.config.*
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import io.github.betterclient.ascendium.ui.utils.DropdownMenu

@Composable
fun SettingEditor(setting: Setting) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(32.dp))
        Text(setting.name, fontSize = 18.sp)
        Spacer(Modifier.width(4.dp))
        when(setting) {
            is BooleanSetting -> {
                var state by remember { mutableStateOf(setting.value) }
                ResetButton { setting.reset(); state = setting.value }
                Checkbox(state, onCheckedChange = {
                    state = it
                    setting.value = it
                    ConfigManager.saveConfig()
                })
            }
            is StringSetting -> {
                var text by remember { mutableStateOf(setting.value) }
                ResetButton { setting.reset(); text = setting.value }
                OutlinedTextField(text, onValueChange = {
                    text = it
                    setting.value = it
                    ConfigManager.saveConfig()
                }, modifier = Modifier.weight(1f), singleLine = true)
            }
            is NumberSetting -> {
                var num by remember { mutableStateOf(setting.value) }
                ResetButton { setting.reset(); num = setting.value }
                Text(String.format("%.1f", num))
                Slider(
                    num.toFloat(),
                    onValueChange = {
                            num = it.toDouble()
                            setting.value = it.toDouble()
                            ConfigManager.saveConfig()
                    },
                    onValueChangeFinished = { ConfigManager.saveConfig() },
                    modifier = Modifier.weight(1f), valueRange = setting.min.toFloat()..setting.max.toFloat()
                )
            }
            is ColorSetting -> {
                ColorPicker(Color(setting.value), Color(setting.defaultValue)) {
                    setting.value = it.rgb
                    ConfigManager.saveConfig()
                }
            }
            is DropdownSetting -> {
                val selected = remember { mutableStateOf(setting.value) }
                ResetButton { setting.reset(); selected.value = setting.value }
                DropdownMenu(
                    modifier = Modifier.weight(1f),
                    theme = AscendiumTheme.colorScheme,
                    options = setting.options,
                    selectedOption = selected
                ) {
                    selected.value = it
                    setting.value = it
                    ConfigManager.saveConfig()
                }
            }
            is InfoSetting -> {} //name already rendered, we dont't have to do anything
        }
        Spacer(Modifier.width(32.dp))
    }
}

@Composable
fun ResetButton(onClick: () -> Unit) {
    IconButton(onClick = {
        onClick()
    }) { Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.background(
        AscendiumTheme.colorScheme.primary,
        RoundedCornerShape(4.dp)
    ).size(20.dp)) }
    Spacer(Modifier.width(4.dp))
}