package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.GpuFormat
import com.mojang.blaze3d.opengl.FrameBufferCache
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

class V262RawOpenGLAdapter() : RawTexture {
    companion object {
        private fun createGlTexture(glId: Int): GlTexture {
            val clazz = GlTexture::class.java

            val constructor = clazz.getDeclaredConstructor(
                Int::class.javaPrimitiveType,
                String::class.java,
                GpuFormat::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                FrameBufferCache::class.java
            )
            constructor.isAccessible = true

            val usage = GpuTexture.USAGE_TEXTURE_BINDING + GpuTexture.USAGE_RENDER_ATTACHMENT
            val label = "OpenGLTextureAdapterAscendium"
            val format = GpuFormat.RGBA8_UNORM
            val width = minecraft.window.fbWidth
            val height = minecraft.window.fbHeight
            val depthOrLayers = 1
            val mipLevels = 1
            val fbc = RenderSystem.getDevice().let {
                val backend = it.javaClass.getDeclaredField("backend").also { it.isAccessible = true }.get(it)
                if (backend.javaClass.simpleName == "VulkanDevice") throw UnsupportedOperationException("Cannot create GLTexture in VulkanDevice")
                backend.javaClass.getDeclaredField("frameBufferCache").also { it.isAccessible = true }.get(backend)
            }

            return constructor.newInstance(usage, label, format, width, height, depthOrLayers, mipLevels, glId, fbc)
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
        Minecraft.getInstance().gameRenderer.gameRenderState().guiRenderState.also {
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