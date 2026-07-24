package io.github.betterclient.ascendium.module.impl.notifications

import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.platform.FrameRecomposer
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import io.github.betterclient.ascendium.bridge.minecraft
import kotlinx.coroutines.Dispatchers

object NotificationScene {
    @OptIn(InternalComposeUiApi::class)
    lateinit var scene: ComposeScene
    @OptIn(InternalComposeUiApi::class)
    lateinit var recomposer: FrameRecomposer

    @OptIn(InternalComposeUiApi::class)
    fun init() {
        val window = minecraft.window
        if (!::scene.isInitialized) {
            val density = Density(1f)
            recomposer = FrameRecomposer(coroutineContext = Dispatchers.Unconfined)
            scene = CanvasLayersComposeScene(
                density = density,
                size = IntSize(window.fbWidth, window.fbHeight),
                frameRecomposer = recomposer
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
