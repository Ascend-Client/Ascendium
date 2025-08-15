package io.github.betterclient.ascendium.module.impl

import io.github.betterclient.ascendium.Bridge
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.module.Renderable
import io.github.betterclient.ascendium.module.config.StringSetting

object FPSMod : HUDModule("FPS", "Display your frames per second.") {
    val template by StringSetting("Template", "%FPS% fps").delegate(this)

    override fun render(renderer: Renderable) {
        renderer.renderText(template.replace("%FPS%", Bridge.client.fps.toString()), 0, 0)
    }
}