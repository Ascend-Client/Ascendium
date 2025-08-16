package io.github.betterclient.ascendium.ui.mods

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.AscendiumTheme
import io.github.betterclient.ascendium.compose.Center
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.compose.rainbowAsState
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.ui.chrome.ComposeChrome
import io.github.betterclient.ascendium.ui.chrome.OffscreenBrowser
import io.github.betterclient.ascendium.ui.move.MoveModuleUI

@Composable
fun ModsUI(smallen: Boolean) {
    AscendiumTheme {
        Center {
            var expanded by remember { mutableStateOf(false) }
            val targetSize: Dp = if (smallen) {
                (if (!expanded) 512 + 256 + 128 else 512 + 128).dp
            } else {
                (if (!expanded) 60 else 512 + 128).dp
            }
            val animatedWidth by animateDpAsState(targetValue = targetSize)

            val targetSize0: Dp = if (smallen) {
                (if (!expanded) 512 + 256 else 512).dp
            } else {
                (if (!expanded) 48 else 512).dp
            }
            val animatedHeight by animateDpAsState(targetValue = targetSize0)

            LaunchedEffect(Unit) {
                expanded = true
            }

            Box(Modifier
                .size(animatedWidth, animatedHeight)
                .background(MaterialTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacity.toFloat()), RoundedCornerShape(32.dp))
                .safeContentPadding()
                .clip(RoundedCornerShape(32.dp))
            ) {
                Row(modifier = Modifier.align(Alignment.TopStart)) {
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        ComposeUI.current.switchTo {
                            MoveModuleUI(ModManager.getHUDModules(), false)
                        }
                    }) {
                        Text("Back")
                    }
                }

                Row(modifier = Modifier.align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        if (OffscreenBrowser.CHROME == null) return@Button
                        ComposeUI.current.switchTo {
                            ComposeChrome()
                        }
                    }) {
                        Text("Ascendium", fontSize = 18.sp, color = rainbowAsState().value)
                    }
                    Spacer(Modifier.width(8.dp))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    ModsContent()
                }
            }
        }
    }
}