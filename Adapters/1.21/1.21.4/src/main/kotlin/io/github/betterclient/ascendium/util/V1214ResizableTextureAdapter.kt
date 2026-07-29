package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.RenderSystem
import io.github.betterclient.ascendium.bridge.ResizableTexture
import io.github.betterclient.ascendium.bridge.vulkanChecker
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import net.vulkanmod.gl.VkGlTexture
import net.vulkanmod.vulkan.texture.VTextureSelector
import net.vulkanmod.vulkan.texture.VulkanImage
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.BackendTexture
import org.lwjgl.opengl.GL11

class V1214ResizableTextureAdapter(
    override var width: Int,
    override var height: Int
) : ResizableTexture {
    init {
        resize(width, height)
    }

    var image: NativeImage? = null
    var texture: NativeImageBackedTexture? = null
    val textureID = Identifier.of("ascendium", "dynamic_texture_${System.nanoTime()}")

    var hasUploaded = false

    override fun close() {
        texture?.close()
    }

    private fun recreateVulkanImageWithColorAttachment(glId: Int) {
        val texture = VkGlTexture.getTexture(glId)

        val oldImage = texture.vulkanImage
        val width = oldImage.width
        val height = oldImage.height
        val format = oldImage.format
        val mipLevels = oldImage.mipLevels
        val usage = oldImage.usage

        val newImage = VulkanImage
            .builder(width, height)
            .setName("Ascendium RenderTarget")
            .setUsage(usage or 0x10)
            .setMipLevels(mipLevels)
            .setLinearFiltering(false)
            .setClamp(true)
            .setFormat(format)
            .createVulkanImage()

        oldImage.free()
        texture.vulkanImage = newImage

        VTextureSelector.bindTexture(newImage)
    }

    override fun resize(width: Int, height: Int) {
        texture?.close()
        image = NativeImage(width, height, false)
        val tex = NativeImageBackedTexture(image)
        texture = tex

        MinecraftClient.getInstance().textureManager.registerTexture(textureID, tex)

        if (!hasUploaded && RenderSystem.isOnRenderThreadOrInit()) {
            texture?.upload()
            hasUploaded = true
            if (vulkanChecker.isVulkan) {
                recreateVulkanImageWithColorAttachment(texture!!.glId)
            }
        }

        this.width = width
        this.height = height
    }

    override fun toSkia(): Either<BackendRenderTarget, BackendTexture> {
        return if (vulkanChecker.isVulkan) {
            val texture = VkGlTexture.getTexture(texture!!.glId).vulkanImage

            Either.left(
                BackendRenderTarget.makeVulkan(
                    width = width,
                    height = height,
                    imagePtr = texture.id,
                    imageTiling = 0, //VK_IMAGE_TILING_OPTIMAL
                    imageLayout = texture.currentLayout,
                    format = texture.format,
                    imageUsageFlags = texture.usage,
                    sampleCnt = 1,
                    levelCnt = texture.mipLevels
                )
            )
        } else {
            Either.right(
                BackendTexture.makeGL(
                    width = width,
                    height = height,
                    isMipmapped = false,
                    textureId = texture!!.glId,
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
            val texture = VkGlTexture.getTexture(texture!!.glId).vulkanImage
            Either.right(texture.id)
        } else {
            Either.left(texture!!.glId)
        }

    override val textureView: Any?
        get() = null
}