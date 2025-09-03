package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.textures.TextureFormat
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.compose.SkiaRenderAdapter
import kotlinx.atomicfu.locks.withLock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.render.state.GuiRenderState
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.texture.GlTexture
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.TextureSetup
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Surface
import org.joml.Matrix3x2fStack
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock
import kotlin.system.measureNanoTime


private val GameRenderer.guiState: GuiRenderState
    get() {
        val guiStateField: Field = GameRenderer::class.java.getDeclaredField("guiState")
        guiStateField.setAccessible(true)

        val guiState: Any? = guiStateField.get(this)
        return guiState as GuiRenderState
    }

class V1218SkiaRenderAdapter : SkiaRenderAdapter {
    val adapter = V1218SkiaRenderAdapterObject()
    override fun withSkia(block: (Canvas) -> Unit) {
        adapter.withSkia(block)
    }

    override fun task(block: () -> Unit) {
        adapter.tasks.add(block)
    }
}

class V1218SkiaRenderAdapterObject {
    private var vpW = 0
    private var vpH = 0

    @Volatile private lateinit var texture: GpuTextureView

    @Volatile
    private var content: ((Canvas) -> Unit)? = null
    @Volatile
    private var frontImage: NativeImage? = null
    private val lock = ReentrantLock()
    val tasks = ConcurrentLinkedQueue<() -> Unit>()

    init {
        Thread(this::backgroundRenderLoop, "Skia-Software-Renderer").apply {
            isDaemon = true
            start()
        }
    }

    fun withSkia(block: (Canvas) -> Unit) {
        this.content = block
        init()

        lock.withLock {
            val imageToDraw = frontImage
            if (imageToDraw != null) {
                if (imageToDraw.width == texture.texture().getWidth(0) && imageToDraw.height == texture.texture().getHeight(0)) {
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture.texture(), imageToDraw)
                }
                val renderState = TexturedQuadGuiElementRenderState(
                    RenderPipelines.GUI_TEXTURED,
                    TextureSetup.withoutGlTexture(texture),
                    Matrix3x2fStack(),
                    0, 0, MinecraftClient.getInstance().window.scaledWidth, MinecraftClient.getInstance().window.scaledHeight,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    -1, null
                )
                MinecraftClient.getInstance().gameRenderer.guiState.addSimpleElement(renderState)
            }
        }
    }

    private fun init() {
        val window = minecraft.window

        if (vpW != window.fbWidth || vpH != window.fbHeight) {
            vpW = window.fbWidth
            vpH = window.fbHeight
            if (::texture.isInitialized) texture.close()
            if (vpW <= 0 || vpH <= 0) return

            texture = RenderSystem.getDevice().createTextureView(
                RenderSystem.getDevice().createTexture(
                    "SkiaBuffer",
                    GlTexture.USAGE_COPY_DST + GlTexture.USAGE_TEXTURE_BINDING + GlTexture.USAGE_RENDER_ATTACHMENT,
                    TextureFormat.RGBA8,
                    vpW, vpH, 1, 1
                )
            )
        }
    }

    private fun backgroundRenderLoop() {
        var softwareSurface: Surface? = null
        var currentW = 0
        var currentH = 0

        while (true) {
            while (true) {
                val item = tasks.poll() ?: break
                item()
            }

            val currentContent = content
            if (currentContent == null) {
                Thread.sleep(10)
                continue
            }

            val vpW = minecraft.window.fbWidth
            val vpH = minecraft.window.fbHeight
            if (vpW != currentW || vpH != currentH) {
                softwareSurface?.close()
                if (vpW > 0 && vpH > 0) {
                    softwareSurface = Surface.makeRasterN32Premul(vpW, vpH)
                    currentW = vpW
                    currentH = vpH
                } else {
                    softwareSurface = null
                }
            }

            val surfaceToDrawOn = softwareSurface ?: continue

            surfaceToDrawOn.canvas.save()
            try {
                surfaceToDrawOn.canvas.clear(Color.TRANSPARENT)
                currentContent(surfaceToDrawOn.canvas)
            } finally {
                surfaceToDrawOn.canvas.restore()
            }

            println(measureNanoTime {
                lock.withLock {
                    if (frontImage == null || frontImage!!.width != surfaceToDrawOn.width || frontImage!!.height != surfaceToDrawOn.height) {
                        frontImage?.close()
                        frontImage = NativeImage(surfaceToDrawOn.width, surfaceToDrawOn.height, false)
                    }

                    val snapshot = surfaceToDrawOn.makeImageSnapshot()
                    snapshot.peekPixels()?.let { pixmap ->
                        val width = pixmap.info.width
                        val height = pixmap.info.height

                        for (y in 0 until height) {
                            for (x in 0 until width) {
                                val rgba = pixmap.getColor(x, y)
                                frontImage!!.setColorArgb(x, y, rgba)
                            }
                        }
                    }
                }
            })
        }
    }
}