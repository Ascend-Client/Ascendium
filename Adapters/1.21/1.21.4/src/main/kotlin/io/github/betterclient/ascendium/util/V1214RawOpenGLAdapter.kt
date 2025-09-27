package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.RawTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class V1214RawOpenGLAdapter() : RawTexture {
    companion object {
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

        val GameRenderer.buffers: BufferBuilderStorage
            get() = GameRenderer::class.java.declaredFields
                .first { it.type == BufferBuilderStorage::class.java }
                .also { it.isAccessible = true }
                .get(this) as BufferBuilderStorage
    }

    override fun render(id: Int) {
        val instance = MinecraftClient.getInstance()
        val window = instance.window

        val consumers = instance.gameRenderer.buffers.entityVertexConsumers
        val renderLayer = RenderLayer.getGuiOpaqueTexturedBackground(
            cache.computeIfAbsent(id) { tex -> generateID(tex) }
        )

        val matrix4f = MatrixStack().peek().getPositionMatrix()
        val (width, height) = window.scaledWidth.toFloat() to window.scaledHeight.toFloat()

        val consumer = consumers.getBuffer(renderLayer)
        consumer.vertex(matrix4f, 0f, 0f, 0.0F).texture(0f, 0f).color(-1)
        consumer.vertex(matrix4f, 0f, height, 0.0F).texture(0f, 1f).color(-1)
        consumer.vertex(matrix4f, width, height, 0.0F).texture(1f, 1f).color(-1)
        consumer.vertex(matrix4f, width, 0f, 0.0F).texture(1f, 0f).color(-1)
    }
}