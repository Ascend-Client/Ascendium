package io.github.betterclient.ascendium.bridge

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class BridgeMixinPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String?) { }

    override fun getRefMapperConfig(): String? { return null }

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String): Boolean {
        return BridgeAdapterManager.shouldApply(mixinClassName)
    }

    override fun acceptTargets(
        myTargets: Set<String?>?,
        otherTargets: Set<String?>?
    ) { }

    override fun getMixins(): List<String?>? { return null }

    override fun preApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) { }

    override fun postApply(
        targetClassName: String?,
        targetClass: ClassNode?,
        mixinClassName: String?,
        mixinInfo: IMixinInfo?
    ) { }
}