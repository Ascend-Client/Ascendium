package io.github.betterclient.ascendium.module.impl

import androidx.compose.runtime.getValue
import io.github.betterclient.ascendium.event.EntityHitEvent
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.module.TextModule

object FPSMod : TextModule("FPS", "Display your frames per second.") {
    val template by string("Template", "%FPS% fps")
    val smooth by boolean("Smooth", true)
    val smoothFPS: Int
        get() {
            _smoothFPS.removeIf { it + 1000 < System.currentTimeMillis() }
            return _smoothFPS.size
        }
    val _smoothFPS = mutableListOf<Long>()

    override fun renderModule(): String {
        return if (smooth) {
            _smoothFPS.add(System.currentTimeMillis())
            template.replace("%FPS%", smoothFPS.toString(), true)
        } else {
            template.replace("%FPS%", client.fps.toString(), true)
        }
    }
}

object PingMod : TextModule("Ping", "Display your ping") {
    val template by string("Template", "%PING%ms")
    override fun renderModule() = template.replace("%PING%", client.ping.toString(), true)
}

object ServerDisplayMod : TextModule("Server Display", "Display the IP of the current server") {
    val template by string("Template", "%IP%")
    override fun renderModule() = template.replace("%IP%", client.server, true)
}

object ReachDisplayMod : TextModule("Reach Display", "Display ") {
    val template by string("Template", "%REACH% blocks")
    val template0 by string("No hits template", "No hits")

    var lastTick = 0L
    var lastTickReach: Double = 0.0

    override fun renderModule() =
        if (lastTick + 2000 > System.currentTimeMillis()) {
            template.replace("%REACH%", lastTickReach.toString().take(3), true)
        } else {
            template0
        }

    @EventTarget
    fun onHit(ev: EntityHitEvent) {
        lastTick = System.currentTimeMillis()
        lastTickReach = ev.distance
    }
}