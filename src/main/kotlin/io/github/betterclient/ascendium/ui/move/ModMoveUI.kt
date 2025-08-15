package io.github.betterclient.ascendium.ui.move

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.AscendiumTheme
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.ui.mods.ModsUI

class MoveModuleUI(val mods: List<HUDModule>) : ComposeUI({
    MoveModuleUI(mods, false)
})

@Composable
fun MoveModuleUI(mods: List<HUDModule>, backToConfig: Boolean) {
    AscendiumTheme {
        if (backToConfig) {
            var start by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { start = true }
            val animWidth by animateDpAsState(if (start) 0.dp else (512+256+128).dp)
            val animHeight by animateDpAsState(if (start) 0.dp else (512+256).dp)
            val round by animateDpAsState(if (start) 0.dp else 32.dp)

            Box(
                Modifier
                    .size(animWidth, animHeight)
                    .background(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                        RoundedCornerShape(round)
                    )
            )
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = {
                if (backToConfig) {
                    ComposeUI.current.switchTo {
                        //TODO: config ui
                    }
                } else {
                    ComposeUI.current.switchTo {
                        ModsUI(false)
                    }
                }
            }) {
                Text(if (backToConfig) "Back" else "Mods")
            }
        }

        LaunchedEffect(Unit) {
            ModuleMover(mods).register()
        }
    }
}

