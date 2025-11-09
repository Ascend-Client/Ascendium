package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.textures.TextureFormat
import io.github.betterclient.ascendium.bridge.RawTexture
import io.github.betterclient.ascendium.bridge.minecraft
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.texture.AbstractTexture
import net.minecraft.client.texture.GlTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import java.lang.reflect.Constructor

class V1215RawOpenGLAdapter() : RawTexture {
    companion object {
        val cache = mutableMapOf<Int, Identifier>()

        fun generateID(texture: Int): Identifier {
            val id = Identifier.of("ascendium", "opengl/texture${System.nanoTime()}.png")

            MinecraftClient.getInstance().textureManager.registerTexture(id, object : AbstractTexture() {
                init {
                    glTexture = createGlTexture(texture)
                }

                private fun createGlTexture(texture: Int, width: Int = minecraft.window.fbWidth, height: Int = minecraft.window.fbHeight): GlTexture? {
                    return try {
                        val constructor: Constructor<GlTexture> = GlTexture::class.java.getDeclaredConstructor(
                            String::class.java,
                            TextureFormat::class.java,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType
                        )
                        constructor.isAccessible = true
                        constructor.newInstance("reflectedTexture", TextureFormat.RGBA8, width, height, 1, texture)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }

                override fun close() {}
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
        val renderLayer = RenderLayer.getGuiTextured(
            cache.computeIfAbsent(id) { tex -> generateID(tex) }
        )

        val matrix4f = MatrixStack().peek().positionMatrix
        val (width, height) = window.scaledWidth.toFloat() to window.scaledHeight.toFloat()

        val consumer = consumers.getBuffer(renderLayer)
        consumer.vertex(matrix4f, 0f, 0f, 0.0F).texture(0f, 0f).color(-1)
        consumer.vertex(matrix4f, 0f, height, 0.0F).texture(0f, 1f).color(-1)
        consumer.vertex(matrix4f, width, height, 0.0F).texture(1f, 1f).color(-1)
        consumer.vertex(matrix4f, width, 0f, 0.0F).texture(1f, 0f).color(-1)
    }
}