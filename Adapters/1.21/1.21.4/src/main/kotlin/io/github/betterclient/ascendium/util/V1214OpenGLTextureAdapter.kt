package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import io.github.betterclient.ascendium.bridge.TextureBridge
import io.github.betterclient.ascendium.bridge.minecraft
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.lang.reflect.Field
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val GameRenderer.buffers: BufferBuilderStorage
    get() {
        val guiStateField: Field = GameRenderer::class.java.declaredFields
            .first { it.type == BufferBuilderStorage::class.java }
        guiStateField.isAccessible = true
        return guiStateField.get(this) as BufferBuilderStorage
    }

private val NativeImage.pointer: Long
    //I'm so sorry.
    get() = this.toString()
        .substringAfter('@', "")
        .takeWhile { it.isDigit() }
        .toLongOrNull() ?: 0L

class V1214OpenGLTextureAdapter : TextureBridge {
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
        } catch (_: Throwable) {}

        lock.withLock {
            //"swap buffers"
            val temp = frontImage
            frontImage = backImage
            backImage = temp
        }
    }

    override fun blit() {
        val dc = DrawContext(MinecraftClient.getInstance(), MinecraftClient.getInstance().gameRenderer.buffers.entityVertexConsumers)
        val id = Identifier.of("ascendium", "opengl_texture${System.currentTimeMillis()}")
        MinecraftClient.getInstance().textureManager.registerTexture(id, NativeImageBackedTexture(frontImage))
        dc.drawGuiTexture(
            RenderLayer::getGuiTextured,
            id,
            0,
            0,
            MinecraftClient.getInstance().window.scaledWidth,
            MinecraftClient.getInstance().window.scaledHeight
        )
    }

    private fun upload(image: BufferedImage, targetImage: NativeImage) {
        if (targetImage.format != NativeImage.Format.RGBA) {
            throw IllegalArgumentException("Target NativeImage must be in RGBA format, but was ${targetImage.format}.")
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
}