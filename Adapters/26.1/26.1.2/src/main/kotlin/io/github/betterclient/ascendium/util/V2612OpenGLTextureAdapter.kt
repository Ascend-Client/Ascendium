package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.opengl.GlSampler
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.AddressMode
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.textures.TextureFormat
import io.github.betterclient.ascendium.bridge.TextureBridge
import io.github.betterclient.ascendium.bridge.minecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.BlitRenderState
import org.jetbrains.skia.BackendRenderTarget
import org.joml.Matrix3x2fStack
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.util.OptionalDouble
import java.util.concurrent.locks.ReentrantLock
import kotlin.compareTo
import kotlin.concurrent.withLock

class V2612OpenGLTextureAdapter() : TextureBridge {
    lateinit var texture: GpuTextureView
    var frontImage: NativeImage? = null
    var backImage: NativeImage? = null
    val lock = ReentrantLock()

    override fun update(image: BufferedImage) {
        val vpW = minecraft.window.fbWidth
        val vpH = minecraft.window.fbHeight

        if (backImage == null || backImage!!.width != vpW || backImage!!.height != vpH) {
            backImage?.close()
            backImage = NativeImage(vpW, vpH, false)
        }

        //draw on back image
        try {
            upload(image, backImage!!)
        } catch (_: Throwable) { }

        lock.withLock {
            //"swap buffers"
            val temp = frontImage
            frontImage = backImage
            backImage = temp
        }
    }

    private fun upload(image: BufferedImage, targetImage: NativeImage) {
        if (targetImage.format() != NativeImage.Format.RGBA) {
            throw IllegalArgumentException("Target NativeImage must be in RGBA format, but was ${targetImage.format()}.")
        }

        if (image.width != targetImage.width || image.height != targetImage.height) {
            throw IllegalArgumentException(
                "Image dimensions do not match. BufferedImage: ${image.width}x${image.height}, " +
                        "NativeImage: ${targetImage.width}x${targetImage.height}."
            )
        }

        if (targetImage.pointer == 0L) {
            throw IllegalArgumentException("Target NativeImage has not been allocated (pointer is 0).")
        }

        val pixelCount = image.width * image.height

        val pixels: IntArray = if (image.type == BufferedImage.TYPE_INT_ARGB) {
            (image.raster.dataBuffer as DataBufferInt).data
        } else {
            IntArray(pixelCount).also {
                image.getRGB(0, 0, image.width, image.height, it, 0, image.width)
            }
        }

        val nativeBuffer = MemoryUtil.memIntBuffer(targetImage.pointer, pixelCount)

        for (i in 0 until pixelCount) {
            val argb = pixels[i]
            val abgr = (argb and 0xFF00FF00.toInt()) or
                    ((argb and 0x00FF0000) shr 16) or
                    ((argb and 0x000000FF) shl 16)

            nativeBuffer.put(i, abgr)
        }
    }

    override fun blit() {
        initTexture()
        lock.withLock {
            frontImage?.let {
                if (it.width == texture.texture().getWidth(0) && it.height == texture.texture().getHeight(0)) {
                    RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture.texture(), it)
                }
            }
        }

        Minecraft.getInstance().gameRenderer.gameRenderState.guiRenderState.also {
            it.up()
        }.addBlitToCurrentLayer(
            BlitRenderState(
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.singleTexture(texture, GlSampler(
                    AddressMode.REPEAT,
                    AddressMode.REPEAT,
                    FilterMode.NEAREST,
                    FilterMode.NEAREST,
                    1,
                    OptionalDouble.empty()
                )),
                Matrix3x2fStack(),
                0,
                0,
                Minecraft.getInstance().window.guiScaledWidth,
                Minecraft.getInstance().window.guiScaledHeight,
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                -1,
                null
            )
        )
    }

    private var vpW = 0
    private var vpH = 0
    fun initTexture() {
        val i = minecraft.window.fbWidth
        val i1 = minecraft.window.fbHeight

        if (vpW != i || vpH != i1) {
            vpW = i
            vpH = i1

            if (::texture.isInitialized) texture.close()
            if (vpW <= 0 || vpH <= 0) return

            texture = RenderSystem.getDevice().createTextureView(
                RenderSystem.getDevice().createTexture(
                    "SkiaBuffer",
                    GpuTexture.USAGE_COPY_DST + GpuTexture.USAGE_TEXTURE_BINDING + GpuTexture.USAGE_RENDER_ATTACHMENT,
                    TextureFormat.RGBA8,
                    vpW, vpH, 1, 1
                )
            )
        }
    }

    override fun close() {
        if (::texture.isInitialized) texture.close()
        frontImage?.close()
        backImage?.close()
    }

    override fun toBackendRenderTarget(): BackendRenderTarget {
        throw UnsupportedOperationException()
    }
}