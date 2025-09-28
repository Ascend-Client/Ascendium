package io.github.betterclient.ascendium.demo.config

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.demo.module.Module

@Composable
fun ModToggle(mod: Module) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.width(32.dp))
        Text("Enabled", fontSize = 18.sp)
        Spacer(Modifier.width(4.dp))
        var enabled by remember { mutableStateOf(mod.enabled) }
        Switch(enabled, onCheckedChange = {
            enabled = it
            if (mod.enabled != it) mod.toggle()
        })
    }
}