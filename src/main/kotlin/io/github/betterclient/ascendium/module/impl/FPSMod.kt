package io.github.betterclient.ascendium.module.impl

import io.github.betterclient.ascendium.Bridge
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.module.Renderable
import io.github.betterclient.ascendium.module.config.StringSetting

object FPSMod : HUDModule("FPS", "Display your frames per second.") {
    val format = StringSetting("Format", "%FPS% fps")
    override fun render(renderer: Renderable) {
        renderer.renderText(format.value.replace("%FPS%", "${Bridge.client.fps}"), 0, 0)
    }
}