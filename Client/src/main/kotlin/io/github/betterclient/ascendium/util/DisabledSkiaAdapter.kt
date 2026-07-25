package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.ui.bridge.SkiaRenderAdapter
import org.jetbrains.skia.Canvas
import kotlin.system.exitProcess

class DisabledSkiaAdapter() : SkiaRenderAdapter {
    override fun withSkia(block: (Canvas) -> Unit) {
        println("The software renderer is disabled!")
        exitProcess(0)
    }

    override fun task(block: () -> Unit) {
        println("The software renderer is disabled!")
        exitProcess(0)
    }
}