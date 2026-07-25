package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.opengl.GlSampler
import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.*
import io.github.betterclient.ascendium.bridge.RawTexture
import io.github.betterclient.ascendium.bridge.minecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.BlitRenderState
import org.joml.Matrix3x2fStack
import java.util.*

class V2612RawOpenGLAdapter() : RawTexture {
    companion object {
        private fun createGlTexture(glId: Int): GlTexture {
            val clazz = GlTexture::class.java

            val constructor = clazz.getDeclaredConstructor(
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
        val texture = TextureSetup.singleTexture(
            map.computeIfAbsent(id) { id ->
                return@computeIfAbsent RenderSystem.getDevice().createTextureView(
                    createGlTexture(id)
                )
            }, GlSampler(
                AddressMode.CLAMP_TO_EDGE,
                AddressMode.CLAMP_TO_EDGE,
                FilterMode.NEAREST,
                FilterMode.NEAREST,
                0, OptionalDouble.empty()
            )
        )

        //texture already exists
        Minecraft.getInstance().gameRenderer.guiState.also {
            it.up()
        }.addBlitToCurrentLayer(
            BlitRenderState(
                RenderPipelines.GUI_TEXTURED,
                texture,
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
}