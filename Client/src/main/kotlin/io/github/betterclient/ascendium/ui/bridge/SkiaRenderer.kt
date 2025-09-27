package io.github.betterclient.ascendium.ui.bridge

import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.bridge.BridgeAdapterManager
import io.github.betterclient.ascendium.bridge.minecraft
import org.jetbrains.skia.*

object ScalingUtils {
    fun <T : Number> getUnscaled(i: T): Float {
        val scaleFactor = minecraft.window.scale
        return ((i.toDouble() * scaleFactor).toFloat())
    }

    fun <T : Number> getScaled(i: T): Float {
        val scaleFactor = minecraft.window.scale
        return ((i.toDouble() / scaleFactor).toFloat())
    }
}

class SkiaRenderer {
    val adapter = if (Ascendium.settings.uiBackend == "Compose") {
        (BridgeAdapterManager.useBridgeUtil({ it.skiaRenderAdapter }) as SkiaRenderAdapter)
    } else {
        OffscreenSkiaRenderer()
    }

    fun withSkia(block: (Canvas) -> Unit) {
        adapter.withSkia(block)
    }

    fun task(block: () -> Unit) {
        //non rendering task that require being run on the same thread as skia (compose input events)
        adapter.task(block)
    }
}

interface SkiaRenderAdapter {
    fun withSkia(block: (Canvas) -> Unit)
    fun task(block: () -> Unit)
}

private val cache = mutableMapOf<Int, Paint>()
fun Int.asPaint() = cache.computeIfAbsent(this) { Paint().apply { color = this@asPaint } }
fun Number.getScaled() = ScalingUtils.getScaled(this)
fun Number.getUnscaled() = ScalingUtils.getUnscaled(this)