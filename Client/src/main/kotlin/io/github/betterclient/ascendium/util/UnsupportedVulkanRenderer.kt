package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.bridge.VulkanRenderer

class UnsupportedVulkanRenderer() : VulkanRenderer {
    override val isVulkan = false
}