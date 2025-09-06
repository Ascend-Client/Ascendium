package io.github.betterclient.ascendium.ui.move

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.module.ComposableHUDModule
import io.github.betterclient.ascendium.ui.bridge.DynamicUI
import io.github.betterclient.ascendium.ui.minecraft.ParallaxBackground
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import io.github.betterclient.ascendium.ui.utils.Center

//TODO: Move module server
class MoveModuleUI(val mods: List<ComposableHUDModule>) : DynamicUI({
    MoveModuleUI(mods, null)
}, { _, _ -> ByteArray(0) })

@Composable
fun MoveModuleUI(mods: List<ComposableHUDModule>, backToConfig: Boolean?) {
    if (minecraft.isWorldNull) {
        ParallaxBackground()
    }
    AscendiumTheme {
        if (backToConfig != null) {
            var start by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { start = true }
            val animWidth by animateDpAsState(if (start) 0.dp else {
                if (backToConfig) {
                    (512+256+128).dp
                } else {
                    (512+128).dp
                }
            }, animationSpec = tween(500))
            val animHeight by animateDpAsState(if (start) 0.dp else {
                if (backToConfig) {
                    (512+256).dp
                } else {
                    512.dp
                }
            }, animationSpec = tween(500))
            val round by animateDpAsState(if (start) 0.dp else 32.dp, animationSpec = tween(500))

            Center {
                Box(
                    Modifier
                        .size(animWidth, animHeight)
                        .background(
                            AscendiumTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat()),
                            RoundedCornerShape(round)
                        )
                )
            }
        }
        Center {
            Button(onClick = {
                if (backToConfig == true) {
                    /*DynamicUI.current.switchTo {
                        ConfigUI(mods[0], false)
                    }*/
                } else {
                    /*DynamicUI.current.switchTo {
                        ModsUI(false)
                    }*/
                }
            }, shape = RoundedCornerShape(16.dp)) {
                Text(if (backToConfig == true) "Back" else "Mods")
            }
        }

        LaunchedEffect(Unit) {
            ModuleMover(mods).register()
        }
    }
}

