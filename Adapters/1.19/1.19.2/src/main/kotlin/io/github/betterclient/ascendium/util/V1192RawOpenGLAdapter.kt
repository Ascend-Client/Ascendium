package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import io.github.betterclient.ascendium.bridge.RawTexture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

class V1192RawOpenGLAdapter() : RawTexture {
    override fun render(id: Int) {
        val instance = MinecraftClient.getInstance()
        val window = instance.window

        val (width, height) = window.scaledWidth to window.scaledHeight

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.enableBlend()
        RenderSystem.setShaderTexture(0, id)

        DrawableHelper.drawTexture(
            MatrixStack(), 0, 0,
            0f, 0f, width, height, width, height
        )
    }
}