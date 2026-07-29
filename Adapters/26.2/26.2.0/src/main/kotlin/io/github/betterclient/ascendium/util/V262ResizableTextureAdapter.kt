package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.GpuFormat
import com.mojang.blaze3d.opengl.GlConst
import com.mojang.blaze3d.opengl.GlTexture
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.*
import com.mojang.blaze3d.vulkan.VulkanGpuTexture
import io.github.betterclient.ascendium.bridge.ResizableTexture
import io.github.betterclient.ascendium.bridge.vulkanChecker
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.BackendTexture
import org.lwjgl.opengl.GL11
import java.util.*

class V262ResizableTextureAdapter(
    override var width: Int,
    override var height: Int,
) : ResizableTexture {
    lateinit var texture: GpuTextureView
    lateinit var sampler: GpuSampler

    init {
        resize(width, height)
    }

    override fun resize(width: Int, height: Int) {
        if (::texture.isInitialized) {
            texture.texture().close()
            texture.close()
        }
        if (width <= 0 || height <= 0) return

        texture = RenderSystem.getDevice().createTextureView(
            RenderSystem.getDevice().createTexture(
                "SkiaBuffer",
                GpuTexture.USAGE_RENDER_ATTACHMENT + GpuTexture.USAGE_TEXTURE_BINDING + GpuTexture.USAGE_COPY_SRC + GpuTexture.USAGE_COPY_DST,
                GpuFormat.RGBA8_UNORM,
                width, height, 1, 1
            )
        )

        if (!::sampler.isInitialized) sampler = RenderSystem.getDevice().createSampler(
            AddressMode.REPEAT,
            AddressMode.REPEAT,
            FilterMode.NEAREST,
            FilterMode.NEAREST,
            1,
            OptionalDouble.empty()
        )

        this.width = width
        this.height = height
    }

    override fun close() {
        if (::sampler.isInitialized) sampler.close()
        if (::texture.isInitialized) {
            texture.texture().close()
            texture.close()
        }
    }

    override fun toSkia(): Either<BackendRenderTarget, BackendTexture> {
        return if (vulkanChecker.isVulkan) {
            val texture = (texture.texture() as VulkanGpuTexture)
            Either.left(
                toBackendTarget(texture)
            )
        } else {
            Either.right(
                BackendTexture.makeGL(
                    width = width,
                    height = height,
                    isMipmapped = texture.mipLevels() > 1,
                    textureId = (texture.texture() as GlTexture).glId(),
                    textureTarget = GL11.GL_TEXTURE_2D,
                    textureFormat = GlConst.toGlInternalId(texture.texture().format),
                )
            )
        }
    }

    override fun recreate() {
        //don't close
        texture = RenderSystem.getDevice().createTextureView(
            RenderSystem.getDevice().createTexture(
                "SkiaBuffer",
                GpuTexture.USAGE_RENDER_ATTACHMENT + GpuTexture.USAGE_TEXTURE_BINDING + GpuTexture.USAGE_COPY_SRC + GpuTexture.USAGE_COPY_DST,
                GpuFormat.RGBA8_UNORM,
                width, height, 1, 1
            )
        )
    }

    override val id: Either<Int, Long>
        get() = if (vulkanChecker.isVulkan) {
            Either.right((texture.texture() as VulkanGpuTexture).vkImage())
        } else {
            Either.left((texture.texture() as GlTexture).glId())
        }

    override val textureView: Any
        get() = texture
}