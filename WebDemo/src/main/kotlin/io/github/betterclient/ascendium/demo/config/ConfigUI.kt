package io.github.betterclient.ascendium.demo.config

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.demo.mods.Mods
import io.github.betterclient.ascendium.demo.module.Module
import io.github.betterclient.ascendium.demo.ui.AscendiumTheme
import io.github.betterclient.ascendium.demo.ui.Center
import io.github.betterclient.ascendium.demo.ui.ParallaxBackground
import io.github.betterclient.ascendium.demo.ui.backgroundOpacity
import io.github.betterclient.ascendium.demo.ui.content
import io.github.betterclient.ascendium.ui.config.ConfigContent

@Composable
fun ConfigUI(mod: Module, fromMods: Boolean) {
    ParallaxBackground()
    AscendiumTheme {
        Center {
            var animate by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { animate = true }
            val boxWidth by animateDpAsState(if (animate) (512+256+128).dp else {
                if (fromMods) (512+128).dp else 48.dp
            })
            val boxHeight by animateDpAsState(if (animate) (512+256).dp else {
                if (fromMods) 512.dp else 48.dp
            })
            var preview by remember { mutableStateOf(false) }
            val bgColor =
                MaterialTheme.colorScheme.background.copy(alpha = backgroundOpacity.toFloat())
            Box(
                Modifier
                    .size(boxWidth, boxHeight)
                    .background(
                        bgColor,
                        RoundedCornerShape(32.dp)
                    )
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Row(modifier = Modifier.align(Alignment.CenterStart)) {
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                content = {
                                    Mods(true)
                                }
                            }) { Text("Back", color = MaterialTheme.colorScheme.onBackground) }
                        }

                        Text(mod.name, color = MaterialTheme.colorScheme.onBackground)

                        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                            Button(onClick = {
                                preview = !preview
                            }) {
                                Text(
                                    text = if (preview) {
                                        "Disable preview"
                                    } else {
                                        "Enable preview"
                                    },
                                    color = MaterialTheme.colorScheme.onBackground) }
                            Spacer(Modifier.width(8.dp))
                        }
                    }

                    Spacer(Modifier.fillMaxWidth().height(1.dp).background(Color.White))

                    ConfigContent(preview, mod)
                }
            }
        }
    }
}