package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.textures.TextureFormat
import io.github.betterclient.ascendium.bridge.ResizableTexture
import io.github.betterclient.ascendium.bridge.vulkanChecker
import net.minecraft.client.texture.GlTexture
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.BackendTexture
import org.lwjgl.opengl.GL11

class V1218ResizableTextureAdapter(
    override var width: Int,
    override var height: Int
) : ResizableTexture {
    init {
        resize(width, height)
    }

    lateinit var texture: GpuTextureView

    override fun resize(width: Int, height: Int) {
        if (::texture.isInitialized) {
            texture.texture().close()
            texture.close()
        }
        texture = RenderSystem.getDevice().createTextureView(
            RenderSystem.getDevice().createTexture(
                "SkiaBuffer",
                GpuTexture.USAGE_COPY_DST + GpuTexture.USAGE_TEXTURE_BINDING + GpuTexture.USAGE_RENDER_ATTACHMENT,
                TextureFormat.RGBA8,
                width, height, 1, 1
            )
        )

        texture.texture().setTextureFilter(
            FilterMode.NEAREST,
            FilterMode.NEAREST,
            false
        )

        this.width = width
        this.height = height
    }

    override fun close() {
        if (::texture.isInitialized) {
            texture.texture().close()
            texture.close()
        }
    }

    override fun toSkia(): Either<BackendRenderTarget, BackendTexture> {
        return if (vulkanChecker.isVulkan) {
            Either.left(
                toBackendTarget(texture.texture())!!
            )
        } else {
            Either.right(
                BackendTexture.makeGL(
                    width = width,
                    height = height,
                    isMipmapped = false,
                    textureId = (texture.texture() as GlTexture).glId,
                    textureTarget = GL11.GL_TEXTURE_2D,
                    textureFormat = GL11.GL_RGBA8
                )
            )
        }
    }

    override fun recreate() {
        resize(width, height)
    }

    override val id: Either<Int, Long>
        get() = if (vulkanChecker.isVulkan) {
            val vulkanImageField = texture.javaClass.getDeclaredField("glTexture").apply { isAccessible = true }
            val glTexture = vulkanImageField.get(texture)
            val getVulkanImageMethod = glTexture.javaClass.getMethod("getVulkanImage")
            val vulkanImage = getVulkanImageMethod.invoke(glTexture)!!
            Either.right(vulkanImage.javaClass.getMethod("getId").invoke(vulkanImage) as Long)
        } else {
            Either.left((texture.texture() as GlTexture).glId)
        }

    override val textureView: Any
        get() = texture
}