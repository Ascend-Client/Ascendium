package io.github.betterclient.ascendium.screen.mods

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.ComposeUI

//TODO: remove after ModMoveUI
class ModsUI : ComposeUI({
    //ModsUI(false)
    //debugging
    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = null,
        tint = Color.Green,
        modifier = Modifier.size(20.dp)
    )
})

@Composable
fun ModsUI(smallen: Boolean) {
    MaterialTheme(darkColorScheme()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f), RoundedCornerShape(32.dp))
                .safeContentPadding()
                .clip(RoundedCornerShape(32.dp))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    ModsContent()
                }
            }
        }
    }
}