package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.github.betterclient.ascendium.bridge.RawTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import org.lwjgl.opengl.GL11

class V1165RawOpenGLAdapter() : RawTexture {
    override fun render(id: Int) {
        val window = MinecraftClient.getInstance().window

        val (width, height) = window.scaledWidth to window.scaledHeight

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableBlend()
        RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.SRC_ALPHA,
            GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SrcFactor.ONE,
            GlStateManager.DstFactor.ZERO
        )

        RenderSystem.disableAlphaTest()
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0f)

        RenderSystem.bindTexture(id)

        DrawableHelper.drawTexture(
            MatrixStack(), 0, 0,
            0f, 0f, width, height, width, height
        )
    }
}