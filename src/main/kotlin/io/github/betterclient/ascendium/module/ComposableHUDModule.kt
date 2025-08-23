package io.github.betterclient.ascendium.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.compose.getScaled
import io.github.betterclient.ascendium.compose.getUnscaled
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.config.ColorSetting
import io.github.betterclient.ascendium.ui.utils.MCFont
import io.github.betterclient.ascendium.ui.utils.ModifyAll

abstract class ComposableHUDModule(name: String, description: String, val hasBackground: Boolean = true) : Module(name, description) {
    var x by mutableStateOf(100)
    var y by mutableStateOf(100)
    val textColor by color("Text Color", -1)
    val backgroundColor = ColorSetting("Background Color", 0x51000000)
    abstract val minecraftFont: Boolean
    var scale by number("Scale", 1.0, 0.25, 3.0)

    private var _width: Int = 0
    private var _height: Int = 0

    val width: Int
        get() = _width
    val height: Int
        get() = _height

    init {
        if (hasBackground) {
            settings.add(backgroundColor)
        }
    }

    @Composable
    abstract fun Render()

    @Composable
    private fun RenderComposable() {
        val density = LocalDensity.current
        val xDp: Dp = remember(x, scale) { with(density) { x.getUnscaled().toDp() } }
        val yDp: Dp = remember(y, scale) { with(density) { y.getUnscaled().toDp() } }

        val bg by backgroundColor.state
        val colorScheme = createScheme(
            textColor = Color(textColor),
            backgroundColor = Color(bg)
        )
        MaterialTheme(
            colorScheme = colorScheme,
            typography =
                Typography()
                    .let { if (minecraftFont) it.MCFont() else it }
                    .ModifyAll { it ->
                        it.copy(color = colorScheme.onBackground)
                    }
        ) {
            Box(
                Modifier
                    .offset(xDp, yDp)
                    .then(
                        if (hasBackground) Modifier.background(
                            color = Color(bg),
                            shape = RoundedCornerShape(8.dp)
                        ) else Modifier
                    )
                    .padding(4.dp)
                    .onGloballyPositioned {
                        _width = it.size.width.getScaled().toInt()
                        _height = it.size.height.getScaled().toInt()
                    }
            ) {
                Render()
            }
        }
    }

    @EventTarget
    @Suppress("Unused")
    fun _render(hudRenderHudEvent: RenderHudEvent) {
        render()
    }

    @OptIn(InternalComposeUiApi::class)
    fun render() {
        SkiaRenderer.withSkia {
            tryInitCompose()
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }
    }

    fun renderAt(x: Int, y: Int, scale: Double) {
        val xo = this.x
        val yo = this.y
        val so = this.scale
        this.x = x
        this.y = y
        this.scale = scale

        render()

        this.x = xo
        this.y = yo
        this.scale = so
    }

    @OptIn(InternalComposeUiApi::class)
    private lateinit var scene: ComposeScene

    @OptIn(InternalComposeUiApi::class)
    private fun tryInitCompose() {
        if (!::scene.isInitialized) {
            val density = Density(scale.toFloat())
            scene = CanvasLayersComposeScene(
                density = density,
                invalidate = {/*Minecraft should schedule?*/}
            )

            scene.setContent({ RenderComposable() })
        } else {
            if (scene.density.density != scale.toFloat()) {
                scene.density = Density(scale.toFloat())
            }
        }
    }

    @Composable
    fun createScheme(
        textColor: Color,
        backgroundColor: Color
    ): ColorScheme {
        return darkColorScheme(
            primary = textColor,
            onPrimary = backgroundColor,
            primaryContainer = textColor.copy(alpha = 0.3f),
            onPrimaryContainer = textColor,
            secondary = textColor.copy(alpha = 0.7f),
            onSecondary = backgroundColor,
            secondaryContainer = textColor.copy(alpha = 0.2f),
            onSecondaryContainer = textColor,
            tertiary = textColor.copy(alpha = 0.5f),
            onTertiary = backgroundColor,
            tertiaryContainer = textColor.copy(alpha = 0.1f),
            onTertiaryContainer = textColor,
            background = backgroundColor,
            onBackground = textColor,
            surface = backgroundColor,
            onSurface = textColor,
            surfaceVariant = backgroundColor.copy(alpha = 0.8f).compositeOver(Color.White.copy(alpha = 0.1f)),
            onSurfaceVariant = textColor
        )
    }
}