package io.github.betterclient.ascendium.module

import io.github.betterclient.ascendium.Bridge
import io.github.betterclient.ascendium.BridgeRenderer
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.event.eventBus
import io.github.betterclient.ascendium.module.config.BooleanSetting
import io.github.betterclient.ascendium.module.config.ColorSetting
import io.github.betterclient.ascendium.module.config.ConfigManager
import io.github.betterclient.ascendium.module.config.DropdownSetting
import io.github.betterclient.ascendium.module.config.NumberSetting
import io.github.betterclient.ascendium.module.config.Setting
import io.github.betterclient.ascendium.module.config.StringSetting

open class Module(val name: String, val description: String) {
    var enabled: Boolean = false
    val settings: MutableList<Setting> = mutableListOf()
    val client
        get() = Bridge.client

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

    fun boolean(name: String, value: Boolean) =
        BooleanSetting(name, value).apply { this@Module.settings.add(this) }::value

    fun number(name: String, value: Double, min: Double = Double.NEGATIVE_INFINITY, max: Double = Double.POSITIVE_INFINITY) =
        NumberSetting(name, value, min, max).apply { this@Module.settings.add(this) }::value

    fun string(name: String, value: String) =
        StringSetting(name, value).apply { this@Module.settings.add(this) }::value

    fun dropdown(name: String, value: String, vararg options: String) =
        DropdownSetting(name, value, options.toList()).apply { this@Module.settings.add(this) }::value

    fun color(name: String, value: Int) =
        ColorSetting(name, value).apply { this@Module.settings.add(this) }::value
}

abstract class HUDModule(name: String, description: String, hasBackground: Boolean = true) : Module(name, description) {
    var x = 100
    var y = 100
    val textColor by color("Text Color", -1)
    val backgroundColor = ColorSetting("Background Color", 0x51000000)
    val minecraftRenderer by boolean("Use Minecraft Renderer", true)
    var scale by number("Scale", 1.0, 0.25, 3.0)

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

    fun renderAt(x: Int, y: Int, scale: Double, context: BridgeRenderer) {
        val xo = this.x
        val yo = this.y
        val so = this.scale
        this.x = x
        this.y = y
        this.scale = scale

        render(context)

        this.x = xo
        this.y = yo
        this.scale = so
    }
}

abstract class TextModule(name: String, description: String) : HUDModule(name, description, true) {
    override fun render(renderer: Renderable) {
        renderer.renderText(render(), 0, 0)
    }

    abstract fun render(): String
}