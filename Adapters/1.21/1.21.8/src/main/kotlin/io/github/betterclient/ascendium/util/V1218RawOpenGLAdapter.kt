package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.textures.TextureFormat
import io.github.betterclient.ascendium.bridge.RawTexture
import io.github.betterclient.ascendium.bridge.minecraft
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState
import net.minecraft.client.texture.GlTexture
import net.minecraft.client.texture.TextureSetup
import org.joml.Matrix3x2fStack
import java.lang.reflect.Constructor

class V1218RawOpenGLAdapter() : RawTexture {
    companion object {
        private fun createGlTexture(glId: Int): GlTexture {
            val clazz = GlTexture::class.java

            val constructor: Constructor<GlTexture> = clazz.getDeclaredConstructor(
                Int::class.javaPrimitiveType,
                String::class.java,
                TextureFormat::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            constructor.isAccessible = true

            val usage = GpuTexture.USAGE_TEXTURE_BINDING + GpuTexture.USAGE_RENDER_ATTACHMENT
            val label = "OpenGLTextureAdapterAscendium"
            val format = TextureFormat.RGBA8
            val width = minecraft.window.fbWidth
            val height = minecraft.window.fbHeight
            val depthOrLayers = 1
            val mipLevels = 1

            return constructor.newInstance(usage, label, format, width, height, depthOrLayers, mipLevels, glId)
        }
        val map = mutableMapOf<Int, GpuTextureView>()

    }

    override fun render(id: Int) {
        val texture = TextureSetup.withoutGlTexture(
            map.computeIfAbsent(id) { id ->
                return@computeIfAbsent RenderSystem.getDevice().createTextureView(
                    createGlTexture(id)
                )
            }
        )

        texture.texure0()!!.texture().setTextureFilter(
            FilterMode.NEAREST,
            FilterMode.NEAREST,
            false
        )
        //texture already exists
        MinecraftClient.getInstance().gameRenderer.guiState.addSimpleElement(
            TexturedQuadGuiElementRenderState(
                RenderPipelines.GUI_TEXTURED,
                texture,
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
        )
    }


}