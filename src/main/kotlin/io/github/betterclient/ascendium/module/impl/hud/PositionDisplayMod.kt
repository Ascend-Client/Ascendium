package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.HUDModule
import java.util.Locale.getDefault

object PositionDisplayMod : HUDModule("Position Display", "Display your position and information") {
    val info = info("Leave blank to disable")
    val xLine by string("X template", "X: %X%")
    val yLine by string("Y template", "Y: %Y%")
    val zLine by string("Z template", "Z: %Z%")
    val biomeLine by string("Biome template", "Biome: %B%")
    val facingLine by string("Facing template", "Facing: %F%")

    var xl by mutableStateOf("")
    var yl by mutableStateOf("")
    var zl by mutableStateOf("")
    var bl by mutableStateOf("")
    var fl by mutableStateOf("")

    @Composable
    override fun Render() {
        previewHeight = 300
        Column {
            if (xl.isNotEmpty()) Text(xl)
            if (yl.isNotEmpty()) Text(yl)
            if (zl.isNotEmpty()) Text(zl)
            if (bl.isNotEmpty()) Text(bl)
            if (fl.isNotEmpty()) Text(fl)
        }
    }

    @Composable
    override fun RenderPreview() {
        previewHeight = 300
        Column {
            if (xLine.isNotEmpty()) Text(xLine.positionTemplatePreview())
            if (yLine.isNotEmpty()) Text(yLine.positionTemplatePreview())
            if (zLine.isNotEmpty()) Text(zLine.positionTemplatePreview())
            if (biomeLine.isNotEmpty()) Text(biomeLine.positionTemplatePreview())
            if (facingLine.isNotEmpty()) Text(facingLine.positionTemplatePreview())
        }
    }

    @EventTarget
    fun update(onUpdate: RenderHudEvent) {
        xl = xLine.positionTemplate()
        yl = yLine.positionTemplate()
        zl = zLine.positionTemplate()
        bl = biomeLine.positionTemplate()
        fl = facingLine.positionTemplate()
    }

    fun String.positionTemplate(): String {
        val player = minecraft.player
        val pos = player.getPos()
        return this
            .replace("%X%", pos.x.toInt().toString(), ignoreCase = true)
            .replace("%Y%", pos.y.toInt().toString(), ignoreCase = true)
            .replace("%Z%", pos.z.toInt().toString(), ignoreCase = true)
            .replace(
                "%B%",
                player.biome
                    .replace("minecraft:", "")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() },
                ignoreCase = true
            )
            .replace("%F%", player.facing, ignoreCase = true)
    }

    fun String.positionTemplatePreview(): String {
        return this
            .replace("%X%", "5", ignoreCase = true)
            .replace("%Y%", "-4", ignoreCase = true)
            .replace("%Z%", "66", ignoreCase = true)
            .replace(
                "%B%",
                "Plains",
                ignoreCase = true
            )
            .replace("%F%", "North", ignoreCase = true)
    }
}