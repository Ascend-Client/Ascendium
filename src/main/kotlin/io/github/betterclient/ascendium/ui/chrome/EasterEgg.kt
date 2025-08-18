package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.ui.mods.ModsUI

@Composable
fun EasterEggUI() {
    Box(Modifier.fillMaxSize()) {
        BrowserView(
            remember { Browser(ChromiumDownloader.client!!, "https://google.com/") },
            modifier = Modifier.size(800.dp, 600.dp).align(Alignment.Center)
        )

        Button(onClick = {
            ComposeUI.current.switchTo { ModsUI(false) }
        }) { Text("Back") }
    }
}