package io.github.betterclient.ascendium.module.impl.notifications

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.github.betterclient.ascendium.minecraft

object NotificationScene {
    @OptIn(InternalComposeUiApi::class)
    lateinit var scene: ComposeScene

    @OptIn(InternalComposeUiApi::class)
    fun init() {
        val window = minecraft.window
        if (!::scene.isInitialized) {
            val density = Density(1f)
            scene = CanvasLayersComposeScene(
                density = density,
                size = IntSize(window.fbWidth, window.fbHeight),
                invalidate = {/*Minecraft should schedule?*/}
            )

            scene.setContent {
                RenderNotificationsHud()
            }
        } else {
            if (window.fbWidth != scene.size!!.width || window.fbHeight != scene.size!!.height) {
                scene.size = IntSize(window.fbWidth, window.fbHeight)
            }
        }
    }
}