package io.github.betterclient.ascendium.module.impl

import io.github.betterclient.ascendium.module.TextModule

object FPSMod : TextModule("FPS", "Display your frames per second.") {
    val template by string("Template", "%FPS% fps")

    override fun render() = template.replace("%FPS%", client.fps.toString())

    override fun onDisable() {

    }
}