package io.github.betterclient.ascendium.ui.mods

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.ui.chrome.ChromiumDownloader
import io.github.betterclient.ascendium.ui.chrome.EasterEggUI
import io.github.betterclient.ascendium.ui.minecraft.ParallaxBackground
import io.github.betterclient.ascendium.ui.move.MoveModuleUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import io.github.betterclient.ascendium.ui.utils.Center
import io.github.betterclient.ascendium.ui.utils.rainbowAsState

@Composable
fun ModsUI(smallen: Boolean) {
    if (minecraft.isWorldNull) {
        ParallaxBackground()
    }
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

            val bgColor = AscendiumTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat())
            Box(Modifier
                .size(animatedWidth, animatedHeight)
                .dropShadow(
                    RoundedCornerShape(32.dp),
                    Shadow(8.dp, bgColor)
                )
                .background(bgColor, RoundedCornerShape(32.dp))
                .safeContentPadding()
            ) {
                Row(modifier = Modifier.align(Alignment.TopStart)) {
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        ComposeUI.current.switchTo {
                            MoveModuleUI(ModManager.getHUDModules(), false)
                        }
                    },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Back")
                    }
                }

                Row(modifier = Modifier.align(Alignment.TopEnd), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        if (!ChromiumDownloader.chromiumDownloaded) return@Button
                        ComposeUI.current.switchTo {
                            EasterEggUI()
                        }
                    }, colors = ButtonDefaults.buttonColors()) {
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