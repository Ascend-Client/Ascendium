package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import io.github.betterclient.ascendium.bridge.RawTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.util.math.MatrixStack

class V1206RawOpenGLAdapter() : RawTexture {
    override fun render(id: Int) {
        val instance = MinecraftClient.getInstance()
        val window = instance.window

        RenderSystem.enableBlend()
        RenderSystem.setShaderTexture(0, id)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)

        val matrix4f = MatrixStack().peek().getPositionMatrix()
        val (width, height) = window.scaledWidth.toFloat() to window.scaledHeight.toFloat()

        val consumer = Tessellator.getInstance().buffer
        consumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        consumer.vertex(matrix4f, 0f, 0f, 0.0F).texture(0f, 0f).next()
        consumer.vertex(matrix4f, 0f, height, 0.0F).texture(0f, 1f).next()
        consumer.vertex(matrix4f, width, height, 0.0F).texture(1f, 1f).next()
        consumer.vertex(matrix4f, width, 0f, 0.0F).texture(1f, 0f).next()

        BufferRenderer.drawWithGlobalProgram(consumer.end())
    }
}