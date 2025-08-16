package io.github.betterclient.ascendium.ui.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.compose.DropdownMenu
import io.github.betterclient.ascendium.module.config.BooleanSetting
import io.github.betterclient.ascendium.module.config.ColorSetting
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.module.config.DropdownSetting
import io.github.betterclient.ascendium.module.config.NumberSetting
import io.github.betterclient.ascendium.module.config.Setting
import io.github.betterclient.ascendium.module.config.StringSetting

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
                //TODO: color picker
                Box(Modifier.size(20.dp).background(Color(setting.value), RoundedCornerShape(2.dp)))
            }
            is DropdownSetting -> {
                val selected = remember { mutableStateOf(setting.value) }
                ResetButton { setting.reset(); selected.value = setting.value }
                DropdownMenu(
                    modifier = Modifier.weight(1f),
                    options = setting.options,
                    selectedOption = selected,
                    onOptionSelected = {
                        selected.value = it
                        setting.value = it
                        ConfigManager.saveConfig()
                    },
                    theme = MaterialTheme.colorScheme,
                    name = setting.name
                )
            }
        }
        Spacer(Modifier.width(32.dp))
    }
}

@Composable
fun ResetButton(onClick: () -> Unit) {
    IconButton(onClick = {
        onClick()
    }) { Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.background(
        MaterialTheme.colorScheme.primary,
        RoundedCornerShape(4.dp)
    ).size(20.dp)) }
    Spacer(Modifier.width(4.dp))
}