package io.github.betterclient.ascendium.ui.mods

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.ui.config.SettingEditor

@Composable
fun ClientTab() {
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(4.dp))
        for (setting in Ascendium.settings.settings) {
            SettingEditor(setting)
            Spacer(Modifier.height(4.dp))
        }
    }
}