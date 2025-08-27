package io.github.betterclient.ascendium.module

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.compose.getScaled
import io.github.betterclient.ascendium.compose.getUnscaled
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.event.eventBus
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.module.config.ColorSetting
import io.github.betterclient.ascendium.ui.utils.MCFont
import io.github.betterclient.ascendium.ui.utils.ModifyAll

abstract class ComposableHUDModule(name: String, description: String, val hasBackground: Boolean = true) : Module(name, description) {
    var previewHeight by mutableStateOf(150)
    var x by mutableStateOf(100)
    var y by mutableStateOf(100)
    val textColor by color("Text Color", -1)
    val backgroundColor = ColorSetting("Background Color", 0x51000000)
    abstract val minecraftFont: Boolean
    var scale by number("Scale", 1.0, 0.25, 3.0)
    open val renderBackground = true

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
    protected abstract fun Render()
    @Composable
    protected abstract fun RenderPreview()

    private var isPreview by mutableStateOf(false)
    @Composable
    fun RenderComposable(overridePreview: Boolean = false) {
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
            Box(Modifier.onGloballyPositioned {
                _width = it.size.width.getScaled().toInt()
                _height = it.size.height.getScaled().toInt()
            }) {
                Box(
                    Modifier
                        .then(if (!overridePreview) Modifier.offset(xDp, yDp) else Modifier)
                        .then(
                            if (hasBackground && renderBackground) {
                                Modifier
                                    .dropShadow(
                                        shape = RoundedCornerShape(8.dp),
                                        shadow = Shadow(color = Color(bg), radius = 16.dp)
                                    )
                                    .background(Color(bg), RoundedCornerShape(8.dp))
                            } else {
                                Modifier
                            }
                        )
                        .padding(4.dp)
                ) {
                    if (isPreview || overridePreview) {
                        RenderPreview()
                    } else {
                        Render()
                    }
                }
            }
        }
    }

    fun renderAt(x: Int, y: Int, scale: Double) {
        val xo = this.x
        val yo = this.y
        val so = this.scale
        this.x = x
        this.y = y
        this.scale = scale

        renderAll(listOf(this), false)

        this.x = xo
        this.y = yo
        this.scale = so
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

    companion object {
        //rendering optimizations
        //render all modules within same skia block

        init {
            eventBus.subscribe()
        }

        @OptIn(InternalComposeUiApi::class)
        @EventTarget
        @Suppress("Unused")
        fun _render(hudRenderHudEvent: RenderHudEvent) {
            renderAll(hud = true)
        }

        @OptIn(InternalComposeUiApi::class)
        fun renderAll(modules: List<ComposableHUDModule> = ModManager.getHUDModules(), hud: Boolean) {
            SkiaRenderer.withSkia {
                tryInitCompose(modules, hud)
                scene.render(it.asComposeCanvas(), System.nanoTime())
            }
        }

        @OptIn(InternalComposeUiApi::class)
        private lateinit var scene: ComposeScene
        private var modulesLast: List<ComposableHUDModule> = listOf()

        @OptIn(InternalComposeUiApi::class)
        private fun tryInitCompose(modules: List<ComposableHUDModule> = ModManager.getHUDModules(), hud: Boolean) {
            val window = minecraft.window
            modules.forEach { it.isPreview = (minecraft.isWorldNull || !hud) }
            if (!::scene.isInitialized) {
                val density = Density(1f)
                scene = CanvasLayersComposeScene(
                    density = density,
                    size = IntSize(window.fbWidth, window.fbHeight),
                    invalidate = {/*Minecraft should schedule?*/}
                )

                scene.setContent({ RenderModules(modules) })
                modulesLast = modules
            } else {
                if (modulesLast != modules) {
                    scene.setContent({ RenderModules(modules) })
                }
                if (window.fbWidth != scene.size!!.width || window.fbHeight != scene.size!!.height) {
                    scene.size = IntSize(window.fbWidth, window.fbHeight)
                }
                modulesLast = modules
            }
        }

        @Composable
        fun RenderModules(modules: List<ComposableHUDModule>) {
            Box(Modifier.fillMaxSize()) {
                for (module in modules) {
                    CompositionLocalProvider(LocalDensity provides Density(module.scale.toFloat())) {
                        module.RenderComposable()
                    }
                }
            }
        }
    }
}