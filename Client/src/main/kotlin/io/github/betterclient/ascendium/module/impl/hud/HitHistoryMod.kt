package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.bridge.IdentifierBridge
import io.github.betterclient.ascendium.event.EntityHitEvent
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.bridge.minecraft
import io.github.betterclient.ascendium.module.HUDModule

object HitHistoryMod : HUDModule("Hit history", "Show your last X hits") {
    val amount by number("Amount", 3.0, 2.0, 5.0)
    val time by number("Time shown", 5.0, 1.0, 20.0)
    val showItem by boolean("Show item", true)
    val showDistance by boolean("Show distance", true)
    val realHitList = mutableStateMapOf<Long, RememberedHit>()

    @Composable
    override fun Render() {
        previewHeight = 300
        Column {
            for (hit in realHitList.values.take(amount.toInt())) {
                RenderHit(hit)
            }

            if (realHitList.isEmpty()) {
                Text("No hits")
            }
        }
    }

    val previewList = listOf(
        RememberedHit(IdentifierBridge("minecraft", "textures/item/netherite_sword.png"), 2.4f, "PreviewTarget1"),
        RememberedHit(IdentifierBridge("minecraft", "textures/item/diamond_sword.png"), 3.4f, "PreviewTarget2"),
        RememberedHit(IdentifierBridge("minecraft", "textures/item/golden_sword.png"), 1.2f, "PreviewTarget3"),
        RememberedHit(IdentifierBridge("minecraft", "textures/item/iron_sword.png"), 5.7f, "PreviewTarget4"),
        RememberedHit(IdentifierBridge("minecraft", "textures/item/wooden_sword.png"), 1.2f, "PreviewTarget5")
    )

    @Composable
    override fun RenderPreview() {
        previewHeight = 300
        Column {
            for (hit in previewList.take(amount.toInt())) {
                RenderHit(hit)
            }
        }
    }

    @Composable
    fun RenderHit(hit: RememberedHit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showItem) {
                remember(hit.item) { minecraft.loadResource(hit.item)?.decodeToImageBitmap() }?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        filterQuality = FilterQuality.None
                    )
                }
                Spacer(Modifier.width(16.dp))
            }

            Text(hit.target)

            if (showDistance) {
                Spacer(Modifier.width(16.dp))
                Text(hit.distance.toString().take(3))
            }
        }
    }

    @EventTarget
    fun onHit(hitEvent: EntityHitEvent) {
        realHitList[System.currentTimeMillis()] = RememberedHit(
            minecraft.player.getMainHandItem().itemIdentifier,
            hitEvent.distance.toFloat(),
            hitEvent.receiver.name
        )
    }

    @EventTarget
    fun onRender(renderHudEvent: RenderHudEvent) {
        val expiredKeys = realHitList.keys.filter { it + (time * 1000) <= System.currentTimeMillis() }
        expiredKeys.forEach { realHitList.remove(it) }
    }
}

data class RememberedHit(val item: IdentifierBridge, val distance: Float, val target: String)