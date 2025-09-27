package io.github.betterclient.ascendium.ui.minecraft

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.bridge.MCScreen
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.module.ModManager
import io.github.betterclient.ascendium.ui.bridge.ComposeUI
import io.github.betterclient.ascendium.ui.move.MoveModuleUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import kotlinx.coroutines.delay
import kotlin.system.exitProcess

private var didMenuAnim by mutableStateOf(false)

object CustomMainMenu : ComposeUI({
    AscendiumTheme {
        ParallaxBackground()
        Box(Modifier.fillMaxSize()) {
            ModeButtons {
                current.onRenderThread {
                    minecraft.openScreen(MoveModuleUI(ModManager.getHUDModules()))
                }
            }
            CornerButtons()
        }

        if (!didMenuAnim) {
            EntranceAnimation {
                didMenuAnim = true //never run again
            }
        }
    }
}) {
    override fun shouldRenderBackground() = false
    override fun shouldCloseOnEsc() = false
}

@Composable
private fun EntranceAnimation(onFinish: () -> Unit) {
    var a by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(200); a = true }

    val animFrac by animateFloatAsState(
        targetValue = if (a) 0.0f else 1.0f,
        animationSpec = tween(durationMillis = 500),
        finishedListener = {
            onFinish()
        }
    )

    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(animFrac)
            .background(Color.Gray)
    )
}

@Composable
private fun BoxScope.CornerButtons() {
    Row(Modifier.align(Alignment.TopEnd), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onClick = {
            ComposeUI.current.onRenderThread {
                minecraft.setScreen(MCScreen.OPTIONS_SCREEN)
            }
        }) {
            Text("Options")
        }

        TextButton(onClick = {
            exitProcess(0)
        }) {
            Text("Quit")
        }
    }
}

@Composable
private fun BoxScope.ModeButtons(onAscend: () -> Unit) {
    Column(Modifier.align(Alignment.Center), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextButton(onAscend, modifier = Modifier.width(512.dp)) {
            Text("Ascendium", fontSize = 72.sp)
        }

        val alpha = 0.7f //this should probably not be configurable
        Spacer(Modifier.height(128.dp))
        Button(
            onClick = {
                ComposeUI.current.onRenderThread {
                    minecraft.setScreen(MCScreen.SELECT_WORLD_SCREEN)
                }
            },
            modifier = Modifier
                .width(512.dp)
                .alpha(alpha)
        ) {
            Text("Singleplayer", fontSize = 24.sp)
        }

        Button(
            onClick = {
                ComposeUI.current.onRenderThread {
                    minecraft.setScreen(MCScreen.MULTIPLAYER_SCREEN)
                }
            },
            modifier = Modifier
                .width(512.dp)
                .alpha(alpha)
        ) {
            Text("Multiplayer", fontSize = 24.sp)
        }

        Button(
            onClick = {
                ComposeUI.current.onRenderThread {
                    minecraft.setScreen(MCScreen.REALMS_MAIN_SCREEN)
                }
            },
            modifier = Modifier
                .width(512.dp)
                .alpha(alpha)
        ) {
            Text("Realms", fontSize = 24.sp)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ParallaxBackground(
    modifier: Modifier = Modifier.fillMaxSize(),
    zoom: Float = 1.2f,
    maxOffset: Dp = 40.dp
) {
    val stopAmt = 4f
    var pointerOffset by remember { mutableStateOf(Offset.Zero) }
    var constraints0 by remember { mutableStateOf(Constraints()) }
    var targetOffset by remember { mutableStateOf(Offset.Zero) }

    BoxWithConstraints(
        modifier = modifier.pointerInput(Unit) {
            while (true) {
                val event = awaitPointerEventScope { awaitPointerEvent() }
                pointerOffset = event.changes.first().position
                val normalizedX = (pointerOffset.x / constraints0.maxWidth - 0.5f) * 2f
                val normalizedY = (pointerOffset.y / constraints0.maxHeight - 0.5f) * 2f
                val target = Offset(
                    (normalizedX * maxOffset.toPx()) / stopAmt,
                    (normalizedY * maxOffset.toPx()) / stopAmt
                )
                targetOffset = target
            }
        }
    ) {
        val imageModifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = targetOffset.x
                translationY = targetOffset.y
                scaleX = zoom
                scaleY = zoom
            }
        LaunchedEffect(constraints) {
            constraints0 = constraints
        }

        Image(
            bitmap = Ascendium::class.java.getResourceAsStream("/assets/ascendium/bg0.png")!!.use { it.readAllBytes() }.decodeToImageBitmap(),
            contentDescription = null,
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    }
}