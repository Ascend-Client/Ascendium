package io.github.betterclient.ascendium.ui.minecraft

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.compose.AWTUtils
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import java.awt.event.MouseEvent

var didAnim by mutableStateOf(false)

@Composable
private fun LoadingScreen() {
    AscendiumTheme {
        ParallaxBackground()
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Ascendium", fontSize = 72.sp)

                Text(CustomLoadingScreen.progressText, fontSize = 36.sp)
            }

            Box(
                Modifier
                    .fillMaxWidth(0.9f)
                    .height(64.dp)
                    .border(16.dp, AscendiumTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.BottomCenter)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(CustomLoadingScreen.progress)
                        .height(64.dp)
                        .background(AscendiumTheme.colorScheme.onPrimaryContainer)
                )
            }
        }

        if (!didAnim) {
            val animateTransition by remember(CustomLoadingScreen.progress) { mutableStateOf(CustomLoadingScreen.progress > 0.95f) }
            val animateProgress by animateFloatAsState(
                if (animateTransition) 1.0f else 0.0f,
                animationSpec = tween(500),
                finishedListener = {
                    CustomLoadingScreen.progressText = "Loading"
                    didAnim = true //never again
                }
            )
            Box(Modifier.fillMaxWidth().fillMaxHeight(animateProgress).background(Color.Gray))
        }
    }
}

@OptIn(InternalComposeUiApi::class)
object CustomLoadingScreen {
    private lateinit var scene: ComposeScene
    var progress by mutableStateOf(0.0f)
    var progressText by mutableStateOf("Initializing")

    fun init() {
        if (!::scene.isInitialized) {
            val window = minecraft.window
            val density = Density(window.scale.toFloat())
            scene = CanvasLayersComposeScene(
                density = density,
                size = IntSize(window.fbWidth, window.fbHeight),
                invalidate = {/*Minecraft should schedule?*/}
            )

            scene.setContent {
                LoadingScreen()
            }
        } else {
            val window = minecraft.window
            val density = Density(window.scale.toFloat().div(2f))
            scene.size = IntSize(window.fbWidth, window.fbHeight)
            scene.density = density
        }
    }

    val myRenderer = SkiaRenderer()
    fun render(progress: Float) {
        myRenderer.task {
            init() //loading screen doesn't send init's correctly?
        }
        this.progress = progress
        val awtMods = AWTUtils.getAwtMods(minecraft.window.windowHandle)
        myRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }

        myRenderer.task {
            scene.sendPointerEvent(
                position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
                eventType = PointerEventType.Move,
                nativeEvent = AWTUtils.MouseEvent(
                    minecraft.mouse.xPos,
                    minecraft.mouse.yPos,
                    awtMods,
                    0, //NO BUTTON
                    MouseEvent.MOUSE_MOVED
                )
            )
        }
    }
}