package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import io.github.betterclient.ascendium.bridge.RawTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack

class V1211RawOpenGLAdapter() : RawTexture {
    override fun render(id: Int) {
        val instance = MinecraftClient.getInstance()
        val window = instance.window

        RenderSystem.enableBlend()
        RenderSystem.setShaderTexture(0, id)
        RenderSystem.setShader(GameRenderer::getPositionTexProgram)

        val matrix4f = MatrixStack().peek().getPositionMatrix()
        val (width, height) = window.scaledWidth.toFloat() to window.scaledHeight.toFloat()

        val consumer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        consumer.vertex(matrix4f, 0f, 0f, 0.0F).texture(0f, 0f).color(-1)
        consumer.vertex(matrix4f, 0f, height, 0.0F).texture(0f, 1f).color(-1)
        consumer.vertex(matrix4f, width, height, 0.0F).texture(1f, 1f).color(-1)
        consumer.vertex(matrix4f, width, 0f, 0.0F).texture(1f, 0f).color(-1)

        BufferRenderer.drawWithGlobalProgram(consumer.end())
    }
}