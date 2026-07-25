package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.VulkanRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.PreferredGraphicsApi

//TODO: make the vulkan renderer a common implementation?
class V262VulkanRenderer() : VulkanRenderer {
    override val isVulkan = Minecraft.getInstance().options.preferredGraphicsBackend().get() == PreferredGraphicsApi.VULKAN
}