package io.github.betterclient.ascendium.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import io.github.betterclient.ascendium.compose.ComposeUI

object AscendiumScreen : ComposeUI({
    ComposeM3Test()
})

@Composable
fun ComposeM3Test() {
    var sliderValue by remember { mutableStateOf(0.5f) }
    var checked by remember { mutableStateOf(false) }
    var counter by remember { mutableStateOf(0) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Material3 Test UI")
            Button(onClick = { counter++ }) {
                Text("Clicked $counter times")
            }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it }
                )
                Text("Enable feature")
            }
            ElevatedCard {
                Box(
                    modifier = Modifier
                        .size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Card Content")
                }
            }
        }
    }
}