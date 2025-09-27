package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.textures.TextureFormat
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.ui.bridge.SkiaRenderAdapter
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
import org.jetbrains.skia.Pixmap
import org.jetbrains.skia.Surface
import org.joml.Matrix3x2fStack
import org.lwjgl.system.MemoryUtil
import java.lang.reflect.Field
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock

val NativeImage.pointer: Long
    get() = this.imageId()

val GameRenderer.guiState: GuiRenderState
    get() {
        val guiStateField: Field = GameRenderer::class.java.declaredFields
            .first { it.type == GuiRenderState::class.java }
        guiStateField.isAccessible = true
        return guiStateField.get(this) as GuiRenderState
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
    private var backImage: NativeImage? = null
    private val lock = ReentrantLock()
    val tasks = ConcurrentLinkedQueue<() -> Unit>()
    val render = AtomicBoolean(true)

    init {
        Thread(this::backgroundRenderLoop, "Skia-Software-Renderer").apply {
            isDaemon = true
            start()
        }
    }

    companion object {
        val UI_SCALE
            get() = 1f
    }

    lateinit var state: TexturedQuadGuiElementRenderState

    fun withSkia(block: (Canvas) -> Unit) {
        if (this.content == null)
            this.content = block
        init()

        lock.withLock {
            val imageToDraw = frontImage
            if (imageToDraw != null) {
                if (imageToDraw.width == texture.texture().getWidth(0) && imageToDraw.height == texture.texture().getHeight(0)) {
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture.texture(), imageToDraw)
                }
                MinecraftClient.getInstance().gameRenderer.guiState.addSimpleElement(state)
            }
        }
        render.set(true)
    }

    private fun init() {
        val window = minecraft.window

        val i = window.fbWidth
        val i1 = window.fbHeight
        if (vpW != i || vpH != i1) {
            vpW = i
            vpH = i1
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

            texture.texture().setTextureFilter(
                FilterMode.NEAREST,
                FilterMode.NEAREST,
                false
            )

            state = TexturedQuadGuiElementRenderState(
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.withoutGlTexture(texture),
                Matrix3x2fStack(),
                0,
                0,
                MinecraftClient.getInstance().window.scaledWidth,
                MinecraftClient.getInstance().window.scaledHeight,
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                -1,
                null
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

            if (!render.get()) continue

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

            if (backImage == null || backImage!!.width != surfaceToDrawOn.width || backImage!!.height != surfaceToDrawOn.height) {
                backImage?.close()
                backImage = NativeImage(surfaceToDrawOn.width, surfaceToDrawOn.height, false)
            }

            val snapshot = surfaceToDrawOn.makeImageSnapshot()
            snapshot.peekPixels()?.let { pixmap ->
                parsePixels(pixmap)
            }
            lock.withLock {
                val temp = frontImage
                frontImage = backImage
                backImage = temp
            }

            render.set(false)
        }
    }

    private fun parsePixels(pixmap: Pixmap) {
        val byteBuffer = ByteBuffer.wrap(pixmap.buffer.bytes).order(ByteOrder.BIG_ENDIAN)
        val intBuffer = byteBuffer.asIntBuffer()

        val basePtr = backImage!!.pointer
        val nThreads = 4
        val pool = Executors.newFixedThreadPool(nThreads)
        val chunk = intBuffer.capacity() / nThreads
        for (t in 0 until nThreads) {
            val start = t * chunk
            val end = if (t == nThreads - 1) intBuffer.capacity() else start + chunk

            pool.submit {
                var i = start
                while (i < end) {
                    val rgba = intBuffer[i]

                    val r = (rgba ushr 24) and 0xFF
                    val g = (rgba ushr 16) and 0xFF
                    val b = (rgba ushr 8) and 0xFF
                    val a = rgba and 0xFF

                    val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
                    MemoryUtil.memPutInt(basePtr + i.toLong() * 4, argb)

                    i++
                }
            }
        }

        pool.shutdown()
        pool.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS)
    }
}