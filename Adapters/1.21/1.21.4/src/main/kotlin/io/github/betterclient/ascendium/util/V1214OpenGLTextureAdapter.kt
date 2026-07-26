package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.github.betterclient.ascendium.bridge.TextureBridge
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.bridge.vulkanChecker
import io.github.betterclient.ascendium.ui.bridge.SkiaRenderer
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.gl.ShaderProgramKeys
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import net.vulkanmod.gl.VkGlTexture
import net.vulkanmod.mixin.texture.image.NativeImageAccessor
import net.vulkanmod.vulkan.texture.VTextureSelector
import net.vulkanmod.vulkan.texture.VulkanImage
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.toImage
import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.min

class V1214OpenGLTextureAdapter() : TextureBridge {
    private val internal = if (vulkanChecker.isVulkan) {
        V1214VulkanTextureAdapter()
    } else V1214SkiaOpenGLTextureAdapter()

    override fun update(image: BufferedImage) {
        internal.update(image)
    }

    override fun blit() {
        internal.blit()
    }

    override fun close() {
        internal.close()
    }

    override fun toBackendRenderTarget(): BackendRenderTarget? {
        return internal.toBackendRenderTarget()
    }
}

private class V1214SkiaOpenGLTextureAdapter : TextureBridge {
    @Volatile private var pendingImage: Image? = null
    private var image: Image? = null
    val renderer = SkiaRenderer()

    override fun update(image: BufferedImage) {
        pendingImage = image.toImage()
    }

    override fun blit() {
        val newImage = pendingImage?.also { pendingImage = null }
        if (newImage != null) image = newImage

        image?.let {
            renderer.withSkia { canvas ->
                canvas.drawImageRect(it, Rect.makeWH(it.width.toFloat(), it.height.toFloat()))
            }
        }
    }

    override fun close() {
        image?.close()
    }

    override fun toBackendRenderTarget(): BackendRenderTarget {
        throw UnsupportedOperationException()
    }
}

private class V1214VulkanTextureAdapter : TextureBridge {
    var frontImage: NativeImage? = null
    var backImage: NativeImage? = null
    var hasUploaded = false

    var texture: NativeImageBackedTexture? = null
    val textureID = Identifier.of("ascendium", "dynamic_texture_${System.nanoTime()}")

    val lock = ReentrantLock()

    override fun update(image: BufferedImage) {
        ensureInitialized()

        try {
            upload(image, backImage!!)
        } catch (_: Throwable) { }

        lock.withLock {
            val tex = texture ?: return
            val fImg = frontImage ?: return
            val bImg = backImage ?: return

            frontImage = bImg
            backImage = fImg

            setImageFieldRaw(tex, frontImage)
            hasUploaded = false
        }
    }

    fun drawTransparentTextureBatched(
        ctx: DrawContext,
        texture: Identifier,
        x: Float,
        y: Float,
        width: Float,
        height: Float
    ) {
        val matrix = ctx.matrices.peek().positionMatrix

        // 2. Instantiate our custom layer
        val customLayer = BatchedGuiTextureLayer(texture)

        // 3. Retrieve the consumer from DrawContext (this queues the draw call)
        val consumer = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers.getBuffer(customLayer)

        // 4. Build the quad. Note: We do NOT call BufferRenderer or end the buffer manually.
        // Minecraft will automatically draw this during the correct, bright GUI pass!
        consumer.vertex(matrix, x, y, 0f).texture(0.0f, 0.0f)
        consumer.vertex(matrix, x, y + height, 0f).texture(0.0f, 1.0f)
        consumer.vertex(matrix, x + width, y + height, 0f).texture(1.0f, 1.0f)
        consumer.vertex(matrix, x + width, y, 0f).texture(1.0f, 0.0f)
    }

    override fun blit() {
        ensureInitialized()

        val ctx = DrawContext(MinecraftClient.getInstance(), MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers)
        lock.withLock {
            val tex = texture ?: return
            val id = textureID

            val img = tex.image ?: return
            val texWidth = img.width
            val texHeight = img.height

            if (MinecraftClient.getInstance().overlay != null) {
                ctx.drawTexture(
                    RenderLayer::getGuiTextured,
                    id,
                    0, 0,
                    0.0f, 0.0f,
                    ctx.scaledWindowWidth, ctx.scaledWindowHeight,
                    texWidth, texHeight,
                    texWidth, texHeight
                )
            } else {
                drawTransparentTextureBatched(
                    ctx,
                    id,
                    0f,
                    0f,
                    ctx.scaledWindowWidth.toFloat(),
                    ctx.scaledWindowHeight.toFloat()
                )
            }
        }
    }

    override fun close() {
        lock.withLock {
            backImage?.close()
            backImage = null

            texture?.close()
            texture = null
            frontImage = null
        }
    }

    override fun toBackendRenderTarget(): BackendRenderTarget? {
        if (!vulkanChecker.isVulkan) return log("No vulkanmod") //only for vulkanmod

        ensureInitialized()

        val tex = texture ?: return log("texture null")
        val id = tex.glId

        return try {
            val texture = VkGlTexture.getTexture(id).vulkanImage

            BackendRenderTarget.makeVulkan(
                width = tex.image!!.width,
                height = tex.image!!.height,
                imagePtr = texture.id,
                imageTiling = 0, //VK_IMAGE_TILING_OPTIMAL
                imageLayout = texture.currentLayout,
                format = texture.format,
                imageUsageFlags = texture.usage,
                sampleCnt = 1,
                levelCnt = texture.mipLevels
            )
        } catch (e: Throwable) {
            log("Error resolving Vulkan backend target: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    private fun log(s: String): BackendRenderTarget? {
        println(s)
        return null
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

    private fun upload(image: BufferedImage, targetImage: NativeImage) {
        if (targetImage.format != NativeImage.Format.RGBA) {
            throw IllegalArgumentException("Target NativeImage must be in RGBA format, but was ${targetImage.format}.")
        }

        if (image.width != targetImage.width || image.height != targetImage.height) {
            throw IllegalArgumentException(
                "Image dimensions do not match. BufferedImage: ${image.width}x${image.height}, " +
                        "NativeImage: ${targetImage.width}x${targetImage.height}."
            )
        }

        val pointer = (targetImage as NativeImageAccessor).pixels
        if (pointer == 0L) {
            throw IllegalArgumentException("Target NativeImage has not been allocated (pointer is 0).")
        }

        val pixelCount = image.width * image.height

        val pixels: IntArray = if (image.type == BufferedImage.TYPE_INT_ARGB) {
            (image.raster.dataBuffer as DataBufferInt).data
        } else {
            IntArray(pixelCount).also {
                image.getRGB(0, 0, image.width, image.height, it, 0, image.width)
            }
        }

        val nativeBuffer = MemoryUtil.memIntBuffer(pointer, pixelCount)

        for (i in 0 until pixelCount) {
            val argb = pixels[i]
            val abgr = (argb and 0xFF00FF00.toInt()) or
                    ((argb and 0x00FF0000) shr 16) or
                    ((argb and 0x000000FF) shl 16)

            nativeBuffer.put(i, abgr)
        }
    }

    fun ensureInitialized() {
        val vpW = minecraft.window.fbWidth
        val vpH = minecraft.window.fbHeight

        if (texture == null || frontImage == null || backImage == null ||
            frontImage!!.width != vpW || frontImage!!.height != vpH) {

            lock.withLock {
                if (texture == null || frontImage == null || backImage == null ||
                    frontImage!!.width != vpW || frontImage!!.height != vpH) {

                    frontImage?.close()
                    backImage?.close()
                    texture?.close()

                    frontImage = NativeImage(vpW, vpH, false)
                    backImage = NativeImage(vpW, vpH, false)

                    val tex = NativeImageBackedTexture(frontImage)
                    texture = tex

                    MinecraftClient.getInstance().textureManager.registerTexture(textureID, tex)
                    if (RenderSystem.isOnRenderThreadOrInit()) {
                        tex.upload()
                        hasUploaded = true
                        recreateVulkanImageWithColorAttachment(tex.glId)
                    }
                }
            }
        }
        if (!hasUploaded && RenderSystem.isOnRenderThreadOrInit()) {
            texture?.upload()
            hasUploaded = true
            recreateVulkanImageWithColorAttachment(texture!!.glId)
        }
    }

    private fun setImageFieldRaw(texture: NativeImageBackedTexture, image: NativeImage?) {
        try {
            IMAGE_FIELD?.set(texture, image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val IMAGE_FIELD = NativeImageBackedTexture::class.java.declaredFields.firstOrNull {
            it.type == NativeImage::class.java
        }?.apply {
            isAccessible = true
        }
    }
}

class BatchedGuiTextureLayer(texture: Identifier) : RenderLayer(
    "custom_gui_textured",
    VertexFormats.POSITION_TEXTURE,
    VertexFormat.DrawMode.QUADS,
    1536,
    false, // hasCrumbling
    true,  // translucent
    {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShader ( ShaderProgramKeys.POSITION_TEX )
        RenderSystem.setShaderTexture(0, texture)
    },
    {
        RenderSystem.disableBlend()
    }
)