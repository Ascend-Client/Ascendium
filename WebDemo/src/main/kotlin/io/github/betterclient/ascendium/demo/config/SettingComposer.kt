package io.github.betterclient.ascendium.demo.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.demo.module.*
import io.github.betterclient.ascendium.demo.ui.DropdownMenu

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
                })
            }
            is StringSetting -> {
                var text by remember { mutableStateOf(setting.value) }
                ResetButton { setting.reset(); text = setting.value }
                OutlinedTextField(text, onValueChange = {
                    text = it
                    setting.value = it
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
                    },
                    onValueChangeFinished = { },
                    modifier = Modifier.weight(1f), valueRange = setting.min.toFloat()..setting.max.toFloat()
                )
            }
            is ColorSetting -> {
                ColorPicker(Color(setting.value), Color(setting._defaultValue)) {
                    setting.value = it.rgb
                }
            }
            is DropdownSetting -> {
                val selected = remember { mutableStateOf(setting.value) }
                ResetButton { setting.reset(); selected.value = setting.value }
                DropdownMenu(
                    modifier = Modifier.weight(1f),
                    theme = MaterialTheme.colorScheme,
                    options = setting.options,
                    selectedOption = selected
                ) {
                    selected.value = it
                    setting.value = it
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
    }) { Icon(imageVector = Replay, contentDescription = null, modifier = Modifier.background(
        MaterialTheme.colorScheme.primary,
        RoundedCornerShape(4.dp)
    ).size(20.dp)) }
    Spacer(Modifier.width(4.dp))
}

val Replay: ImageVector
    get() {
        if (_Replay != null) return _Replay!!

        _Replay = ImageVector.Builder(
            name = "Replay",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(480f, 880f)
                quadToRelative(-75f, 0f, -140.5f, -28.5f)
                reflectiveQuadToRelative(-114f, -77f)
                reflectiveQuadToRelative(-77f, -114f)
                reflectiveQuadTo(120f, 520f)
                horizontalLineToRelative(80f)
                quadToRelative(0f, 117f, 81.5f, 198.5f)
                reflectiveQuadTo(480f, 800f)
                reflectiveQuadToRelative(198.5f, -81.5f)
                reflectiveQuadTo(760f, 520f)
                reflectiveQuadToRelative(-81.5f, -198.5f)
                reflectiveQuadTo(480f, 240f)
                horizontalLineToRelative(-6f)
                lineToRelative(62f, 62f)
                lineToRelative(-56f, 58f)
                lineToRelative(-160f, -160f)
                lineToRelative(160f, -160f)
                lineToRelative(56f, 58f)
                lineToRelative(-62f, 62f)
                horizontalLineToRelative(6f)
                quadToRelative(75f, 0f, 140.5f, 28.5f)
                reflectiveQuadToRelative(114f, 77f)
                reflectiveQuadToRelative(77f, 114f)
                reflectiveQuadTo(840f, 520f)
                reflectiveQuadToRelative(-28.5f, 140.5f)
                reflectiveQuadToRelative(-77f, 114f)
                reflectiveQuadToRelative(-114f, 77f)
                reflectiveQuadTo(480f, 880f)
            }
        }.build()

        return _Replay!!
    }

private var _Replay: ImageVector? = null