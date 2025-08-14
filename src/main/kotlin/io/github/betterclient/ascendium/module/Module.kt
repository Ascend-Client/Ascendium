package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.event.eventBus

open class Module(val name: String, val description: String) {
    var enabled: Boolean = false
    val settings: MutableList<Setting> = mutableListOf()

    open fun toggle() {
        enabled = !enabled
        if (enabled) {
            onEnable()
            eventBus.subscribe()
        } else {
            onDisable()
            eventBus.unsubscribe()
        }
    }

    open fun onEnable() {}
    open fun onDisable() {}
}

abstract class HUDModule(name: String, description: String, hasBackground: Boolean = true) : Module(name, description) {
    var x = 100
    var y = 100
    val textColor = ColorSetting("Text Color", -1)
    val backgroundColor = ColorSetting("Background Color", 0x51000000)
    val minecraftRenderer = BooleanSetting("Use Minecraft Renderer", true)
    val scale = NumberSetting("Scale", 1.0, 0.25, 3.0)

    val width: Int
        get() {
            val renderable = Renderable(this, NullRenderer(scale.value.toFloat(), minecraftRenderer.value))
            render(renderable)
            return renderable.width
        }
    val height: Int
        get() {
            val renderable = Renderable(this, NullRenderer(scale.value.toFloat(), minecraftRenderer.value))
            render(renderable)
            return renderable.height
        }

    init {
        if (hasBackground) {
            settings.add(backgroundColor)
        }

        settings.add(textColor)
        settings.add(minecraftRenderer)
        settings.add(scale)
    }

    abstract fun render(renderer: Renderable)

    @EventTarget
    fun _render(hudRenderHudEvent: RenderHudEvent) {
        render(hudRenderHudEvent.renderer)
    }

    fun render(context: BridgeRenderer) {
        val nullR = Renderable(this, NullRenderer(scale.value.toFloat(), minecraftRenderer.value))
        render(nullR)

        if (minecraftRenderer.value) {
            val renderer = Renderable(this, Renderer(scale.value.toFloat(), context, null))
            renderer.renderBG(nullR)

            render(renderer)
        } else {
            SkiaRenderer.withSkia {
                val renderer = Renderable(this, Renderer(scale.value.toFloat(), null, it))
                renderer.renderBG(nullR)

                render(renderer)
            }
        }
    }
}