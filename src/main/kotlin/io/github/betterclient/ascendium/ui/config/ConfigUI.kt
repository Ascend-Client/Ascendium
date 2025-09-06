package io.github.betterclient.ascendium.ui.config

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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.module.ComposableHUDModule
import io.github.betterclient.ascendium.module.Module
import io.github.betterclient.ascendium.ui.bridge.DynamicUI
import io.github.betterclient.ascendium.ui.minecraft.ParallaxBackground
import io.github.betterclient.ascendium.ui.mods.ModsUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import io.github.betterclient.ascendium.ui.utils.Center

@Composable
fun ConfigUI(mod: Module, fromMods: Boolean) {
    if (minecraft.isWorldNull) {
        ParallaxBackground()
    }
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
                AscendiumTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat())
            Box(
                Modifier
                    .size(boxWidth, boxHeight)
                    .dropShadow(
                        RoundedCornerShape(32.dp),
                        Shadow(8.dp, bgColor)
                    )
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
                                //TODO: Mods UI Server
                                DynamicUI.current.switchTo() { ModsUI(true) }
                            }) { Text("Back", color = AscendiumTheme.colorScheme.onBackground) }
                        }

                        Text(mod.name, color = AscendiumTheme.colorScheme.onBackground)

                        if (mod !is ComposableHUDModule) return@Box //don't make preview button if not renderable
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
                                    color = AscendiumTheme.colorScheme.onBackground) }
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