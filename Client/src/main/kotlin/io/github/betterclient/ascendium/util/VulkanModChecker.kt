package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.VulkanChecker
import io.github.betterclient.ascendium.bridge.VulkanContext
import net.fabricmc.loader.api.FabricLoader

class VulkanModChecker() : VulkanChecker {
    override val isVulkan = FabricLoader.getInstance().isModLoaded("vulkanmod")

    override fun getVulkanContext(): VulkanContext {
        if (!isVulkan) {
            throw UnsupportedOperationException("VulkanMod is not loaded!")
        }

        return try {
            val deviceManagerClass = Class.forName("net.vulkanmod.vulkan.device.DeviceManager")
            val queueClass = Class.forName("net.vulkanmod.vulkan.queue.Queue")

            val vkDevice = deviceManagerClass.getField("vkDevice").get(null)
            val physicalDevice = deviceManagerClass.getField("physicalDevice").get(null)

            val devicePtr = vkDevice.javaClass.getMethod("address").invoke(vkDevice) as Long
            val physicalDevicePtr = physicalDevice.javaClass.getMethod("address").invoke(physicalDevice) as Long

            val vkInstance = physicalDevice.javaClass.getMethod("getInstance").invoke(physicalDevice)
            val instancePtr = vkInstance.javaClass.getMethod("address").invoke(vkInstance) as Long

            val graphicsQueue = deviceManagerClass.getMethod("getGraphicsQueue").invoke(null)
            val queueMethod = graphicsQueue.javaClass.methods.firstOrNull { it.name == "vkQueue" || it.name == "queue" } ?: throw NoSuchMethodException()

            val vkQueue = queueMethod.invoke(graphicsQueue)
            val queuePtr = vkQueue.javaClass.getMethod("address").invoke(vkQueue) as Long

            val queueFamilies = queueClass.getMethod("getQueueFamilies").invoke(null)
            val graphicsQueueIndex = queueFamilies.javaClass.getField("graphicsFamily").getInt(queueFamilies)

            val vkClass = Class.forName("org.lwjgl.vulkan.VK")
            val funcProvider = vkClass.getMethod("getFunctionProvider").invoke(null)
            val functionProviderInterface = Class.forName("org.lwjgl.system.FunctionProvider")
            val getFuncAddrMethod = functionProviderInterface.getMethod("getFunctionAddress", CharSequence::class.java)

            val instanceProcAddr = getFuncAddrMethod.invoke(funcProvider, "vkGetInstanceProcAddr") as Long
            val deviceProcAddr = getFuncAddrMethod.invoke(funcProvider, "vkGetDeviceProcAddr") as Long

            VulkanContext(
                instancePtr = instancePtr,
                devicePtr = devicePtr,
                physicalDevicePtr = physicalDevicePtr,
                queuePtr = queuePtr,
                graphicsQueueIndex = graphicsQueueIndex,
                instanceProcAddr = instanceProcAddr,
                deviceProcAddr = deviceProcAddr,
                apiVersion = 4202496
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to extract VulkanContext from VulkanMod", e)
        }
    }
}