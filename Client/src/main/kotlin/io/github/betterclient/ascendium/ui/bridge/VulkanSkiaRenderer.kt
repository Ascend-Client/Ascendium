package io.github.betterclient.ascendium.ui.bridge

import io.github.betterclient.ascendium.bridge.VulkanContext
import io.github.betterclient.ascendium.bridge.createOpenGLTexture
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.bridge.vulkanChecker
import org.jetbrains.skia.*

class VulkanSkiaRenderer() : SkiaRenderAdapter {
    private var vpW = 0
    private var vpH = 0
    var data = vulkanChecker.getVulkanContext()
    var context = genContext(data)
    var activeTexture = createOpenGLTexture()
    lateinit var backendRenderTarget: BackendRenderTarget
    lateinit var surface: Surface

    override fun task(block: () -> Unit) {
        block()
    }

    override fun withSkia(block: (Canvas) -> Unit) {
        if (vpW != minecraft.window.fbWidth || vpH != minecraft.window.fbHeight) {
            vpW = minecraft.window.fbWidth
            vpH = minecraft.window.fbHeight

            activeTexture.close()
            activeTexture = createOpenGLTexture()

            data = vulkanChecker.getVulkanContext()
            context = genContext(data)

            if (::backendRenderTarget.isInitialized) backendRenderTarget.close()
            backendRenderTarget = activeTexture.toBackendRenderTarget()?: return

            if (::surface.isInitialized) surface.close()
            surface = Surface.makeFromBackendRenderTarget(
                context = context,
                rt = backendRenderTarget,
                origin = SurfaceOrigin.TOP_LEFT,
                colorFormat = SurfaceColorFormat.RGBA_8888,
                colorSpace = ColorSpace.sRGB
            )!!
        }

        surface.canvas.let {
            it.clear(Color.TRANSPARENT)

            block(it)
        }
        context.flushAndSubmit(syncCpu = true, surface = surface)

        activeTexture.blit()
    }

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