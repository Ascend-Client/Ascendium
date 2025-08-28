package io.github.betterclient.ascendium.module.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.module.Module
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme

object Notifications : Module("Notifications", "Show notifications for in-game events on supported servers") {
    @OptIn(InternalComposeUiApi::class)
    lateinit var scene: ComposeScene

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onRender(event: RenderHudEvent) {
        init()
        SkiaRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }
    }

    @OptIn(InternalComposeUiApi::class)
    private fun init() {
        val window = minecraft.window
        if (!::scene.isInitialized) {
            val density = Density(1f)
            scene = CanvasLayersComposeScene(
                density = density,
                size = IntSize(window.fbWidth, window.fbHeight),
                invalidate = {/*Minecraft should schedule?*/}
            )

            scene.setContent {
                RenderNotifications()
            }
        } else {
            if (window.fbWidth != scene.size!!.width || window.fbHeight != scene.size!!.height) {
                scene.size = IntSize(window.fbWidth, window.fbHeight)
            }
        }
    }

    @Composable
    private fun RenderNotifications() {
        AscendiumTheme {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier
                            //TODO: animate offset
                            .offset((-10).dp)
                            .background(
                                AscendiumTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat()),
                                RoundedCornerShape(16.dp)
                            )
                            .size(250.dp, 150.dp)
                    ) {
                        IconButton(
                            onClick = {

                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Big Text",
                                style = AscendiumTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Small text below the big one",
                                style = AscendiumTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Bottom Button")
                            }
                        }
                    }
                }
            }
        }
    }
}