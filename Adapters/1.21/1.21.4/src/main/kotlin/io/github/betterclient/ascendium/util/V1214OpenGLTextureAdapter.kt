package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.TextureBridge
import io.github.betterclient.ascendium.bridge.minecraft
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.toImage
import java.awt.image.BufferedImage

class V1214OpenGLTextureAdapter() : TextureBridge {
    @Volatile private var pendingImage: Image? = null
    private var image: Image? = null

    override fun update(image: BufferedImage) {
        pendingImage = image.toImage()
    }

    override fun blit() {
        val newImage = pendingImage?.also { pendingImage = null }
        if (newImage != null) image = newImage

        image?.let {
            V1214SkiaRenderAdapterObject.withSkia { canvas ->
                canvas.drawImageRect(it, Rect.makeWH(it.width.toFloat(), it.height.toFloat()))
            }
        }
    }
}

val GameRenderer.buffers: BufferBuilderStorage
    get() = GameRenderer::class.java.declaredFields
        .first { it.type == BufferBuilderStorage::class.java }
        .also { it.isAccessible = true }
        .get(this) as BufferBuilderStorage

val cache = mutableMapOf<Int, Identifier>()

fun generateID(texture: Int): Identifier {
    val id = Identifier.of("ascendium", "opengl/texture${System.nanoTime()}.png")

    MinecraftClient.getInstance().textureManager.registerTexture(id, object : AbstractTexture() {
        init {
            glId = texture
        }

        override fun close() {}
        override fun clearGlId() {}
    })

    return id
}

fun render(texture: Int) {
    val instance = MinecraftClient.getInstance()
    val window = instance.window

    val consumers = instance.gameRenderer.buffers.entityVertexConsumers
    val renderLayer = RenderLayer.getGuiOpaqueTexturedBackground(
        cache.computeIfAbsent(texture) { tex -> generateID(tex) }
    )

    val matrix4f = MatrixStack().peek().getPositionMatrix()
    val (width, height) = window.scaledWidth.toFloat() to window.scaledHeight.toFloat()

    val consumer = consumers.getBuffer(renderLayer)
    consumer.vertex(matrix4f, 0f, 0f, 0.0F).texture(0f, 0f).color(-1)
    consumer.vertex(matrix4f, 0f, height, 0.0F).texture(0f, 1f).color(-1)
    consumer.vertex(matrix4f, width, height, 0.0F).texture(1f, 1f).color(-1)
    consumer.vertex(matrix4f, width, 0f, 0.0F).texture(1f, 0f).color(-1)
}