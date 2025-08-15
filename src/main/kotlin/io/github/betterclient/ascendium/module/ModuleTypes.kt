package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.event.eventBus
import io.github.betterclient.ascendium.module.config.BooleanSetting
import io.github.betterclient.ascendium.module.config.ColorSetting
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.module.config.NumberSetting
import io.github.betterclient.ascendium.module.config.Setting

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
        ConfigManager.saveConfig()
    }

    open fun onEnable() {}
    open fun onDisable() {}
}

abstract class HUDModule(name: String, description: String, hasBackground: Boolean = true) : Module(name, description) {
    var x = 100
    var y = 100
    val textColor by ColorSetting("Text Color", -1).delegate(this)
    val backgroundColor = ColorSetting("Background Color", 0x51000000)
    val minecraftRenderer by BooleanSetting("Use Minecraft Renderer", true).delegate(this)
    val scale by NumberSetting("Scale", 1.0, 0.25, 3.0).delegate(this)

    val width: Int
        get() {
            val renderable = Renderable(this, NullRenderer(scale.toFloat(), minecraftRenderer))
            render(renderable)
            return renderable.width
        }
    val height: Int
        get() {
            val renderable = Renderable(this, NullRenderer(scale.toFloat(), minecraftRenderer))
            render(renderable)
            return renderable.height
        }

    init {
        if (hasBackground) {
            settings.add(backgroundColor)
        }
    }

    abstract fun render(renderer: Renderable)

    @EventTarget
    fun _render(hudRenderHudEvent: RenderHudEvent) {
        render(hudRenderHudEvent.renderer)
    }

    fun render(context: BridgeRenderer) {
        val nullR = Renderable(this, NullRenderer(scale.toFloat(), minecraftRenderer))
        render(nullR)

        if (minecraftRenderer) {
            val renderer = Renderable(this, Renderer(scale.toFloat(), context, null))
            renderer.renderBG(nullR)

            render(renderer)
        } else {
            SkiaRenderer.withSkia {
                val renderer = Renderable(this, Renderer(scale.toFloat(), null, it))
                renderer.renderBG(nullR)

                render(renderer)
            }
        }
    }
}