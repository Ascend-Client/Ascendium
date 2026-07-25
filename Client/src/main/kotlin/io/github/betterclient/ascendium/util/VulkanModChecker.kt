package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.VulkanChecker
import io.github.betterclient.ascendium.bridge.VulkanContext
import net.fabricmc.loader.api.FabricLoader

class VulkanModChecker() : VulkanChecker {
    override val isVulkan = false
    //FabricLoader.getInstance().isModLoaded("vulkanmod")
    //TODO: re enable once vulkan support is real

    override fun getVulkanContext(): VulkanContext {
        throw UnsupportedOperationException()
    }
}