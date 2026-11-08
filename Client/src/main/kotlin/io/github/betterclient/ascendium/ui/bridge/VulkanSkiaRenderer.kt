package io.github.betterclient.ascendium.ui.bridge

import io.github.betterclient.ascendium.bridge.VulkanContext
import io.github.betterclient.ascendium.bridge.createOpenGLTexture
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.bridge.vulkanChecker
import org.jetbrains.skia.*
import org.jetbrains.skiko.ExperimentalSkikoApi

class VulkanSkiaRenderer() : SkiaRenderAdapter {
    private var vpW = 0
    private var vpH = 0
    var data = vulkanChecker.getVulkanContext()
    var context = genContext(data)
    var activeTexture = createOpenGLTexture()
    lateinit var backendRenderTarget: BackendRenderTarget
    lateinit var surface: Surface

    override val myContext: DirectContext
        get() = context

    override fun task(block: () -> Unit) {
        block()
    }

    override fun withSkia(block: (Canvas) -> Unit) {
        val width = minecraft.window.fbWidth
        val height = minecraft.window.fbHeight
        if (width <= 0 || height <= 0) return
        if (vpW != width || vpH != height || !::backendRenderTarget.isInitialized || !::surface.isInitialized) {
            vpW = width
            vpH = height

            if (::surface.isInitialized) surface.close()
            if (::backendRenderTarget.isInitialized) backendRenderTarget.close()
            context.close()

            activeTexture.close()
            activeTexture = createOpenGLTexture()

            data = vulkanChecker.getVulkanContext()
            context = genContext(data)

            backendRenderTarget = activeTexture.toBackendRenderTarget() ?: return
            surface = Surface.makeFromBackendRenderTarget(
                context = context,
                rt = backendRenderTarget,
                origin = SurfaceOrigin.TOP_LEFT,
                colorFormat = SurfaceColorFormat.RGBA_8888,
                colorSpace = ColorSpace.sRGB
            )!!
        }
        if (!::surface.isInitialized) {
            activeTexture.blit()
            return
        }

        surface.canvas.let {
            it.clear(Color.TRANSPARENT)

            block(it)
        }
        context.flushAndSubmit(syncCpu = true, surface = surface)

        activeTexture.blit()
    }

    @OptIn(ExperimentalSkikoApi::class)
    private fun genContext(data: VulkanContext) = DirectContext.makeVulkan(
        instancePtr = data.instancePtr,
        physicalDevicePtr = data.physicalDevicePtr,
        devicePtr = data.devicePtr,
        queuePtr = data.queuePtr,
        graphicsQueueIndex = data.graphicsQueueIndex,
        instanceProcAddr = data.instanceProcAddr,
        deviceProcAddr = data.deviceProcAddr,
        apiVersion = data.apiVersion,
        memoryAllocator = null
    )
}