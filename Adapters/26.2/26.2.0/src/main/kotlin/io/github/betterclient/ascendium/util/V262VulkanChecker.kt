package io.github.betterclient.ascendium.util

import com.mojang.blaze3d.systems.GpuDevice
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vulkan.VulkanDevice
import io.github.betterclient.ascendium.bridge.VulkanChecker
import io.github.betterclient.ascendium.bridge.VulkanContext
import org.lwjgl.vulkan.VK
import org.lwjgl.vulkan.VK12
import java.lang.reflect.Field

class V262VulkanChecker() : VulkanChecker {
    override val isVulkan: Boolean
        get() {
            val device = RenderSystem.tryGetDevice() ?: return false
            val field = BACKEND_FIELD ?: return false
            return field.get(device) is VulkanDevice
        }

    private companion object {
        private val BACKEND_FIELD: Field? = runCatching {
            GpuDevice::class.java.getDeclaredField("backend").apply {
                isAccessible = true
            }
        }.getOrNull()
    }

    override fun getVulkanContext(): VulkanContext {
        val device = RenderSystem.getDevice()
        val field = BACKEND_FIELD?: throw UnsupportedOperationException("Unable to access GpuDevice.backend via reflection")
        val backend = field.get(device)

        if (backend !is VulkanDevice) {
            throw UnsupportedOperationException("Vulkan backend is not currently active!")
        }

        val vkInstance = backend.instance().vkInstance()
        val vkDevice = backend.vkDevice()
        val physicalDevice = vkDevice.physicalDevice
        val graphicsQueue = backend.graphicsQueue()
        val functionProvider = VK.getFunctionProvider()

        return VulkanContext(
            instancePtr = vkInstance.address(),
            devicePtr = vkDevice.address(),
            physicalDevicePtr = physicalDevice.address(),
            queuePtr = graphicsQueue.vkQueue().address(),
            graphicsQueueIndex = graphicsQueue.queueFamilyIndex(),
            instanceProcAddr = functionProvider.getFunctionAddress("vkGetInstanceProcAddr"),
            deviceProcAddr = functionProvider.getFunctionAddress("vkGetDeviceProcAddr"),
            apiVersion = VK12.VK_API_VERSION_1_2
        )
    }
}