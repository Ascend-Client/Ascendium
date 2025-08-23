package io.github.betterclient.ascendium.ui.move

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.module.ComposableHUDModule
import io.github.betterclient.ascendium.ui.config.ConfigUI
import io.github.betterclient.ascendium.ui.mods.ModsUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import io.github.betterclient.ascendium.ui.utils.Center

class MoveModuleUI(val mods: List<ComposableHUDModule>) : ComposeUI({
    MoveModuleUI(mods, null)
})

@Composable
fun MoveModuleUI(mods: List<ComposableHUDModule>, backToConfig: Boolean?) {
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
                            MaterialTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat()),
                            RoundedCornerShape(round)
                        )
                )
            }
        }
        Center {
            Button(onClick = {
                if (backToConfig == true) {
                    ComposeUI.current.switchTo {
                        ConfigUI(mods[0], false)
                    }
                } else {
                    ComposeUI.current.switchTo {
                        ModsUI(false)
                    }
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

