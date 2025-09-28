package io.github.betterclient.ascendium.demo.mods

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.demo.config.SettingEditor
import io.github.betterclient.ascendium.demo.ui._bo
import io.github.betterclient.ascendium.demo.ui._t

@Composable
fun ClientTab() {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(4.dp))

        SettingEditor(_t)
        Spacer(Modifier.height(4.dp))

        SettingEditor(_bo)
        Spacer(Modifier.height(4.dp))
    }
}