package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.runtime.getValue
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.MouseClickEvent
import io.github.betterclient.ascendium.module.TextModule

object CPSMod : TextModule("CPS", "Shows how many times you click per second") {
    val template by string("template", "%LMB% | %RMB%")
    val lmb: Int
        get() {
            _lmb.removeIf { it + 1000 < System.currentTimeMillis() }
            return _lmb.size
        }
    val _lmb = mutableListOf<Long>()

    val rmb: Int
        get() {
            _rmb.removeIf { it + 1000 < System.currentTimeMillis() }
            return _rmb.size
        }
    val _rmb = mutableListOf<Long>()
    
    override fun renderModule() =
        template
            .replace("%LMB%", lmb.toString(), true)
            .replace("%RMB%", rmb.toString(), true)

    override fun renderPreview() = template
        .replace("%LMB%", "6")
        .replace("%RMB%", "5")

    @EventTarget
    fun onMouseClick(mouseClick: MouseClickEvent) {
        if (!(mouseClick.pressed)) return

        if (mouseClick.button == 0) {
            _lmb.add(System.currentTimeMillis())
        } else if (mouseClick.button == 1) {
            _rmb.add(System.currentTimeMillis())
        }
    }
}