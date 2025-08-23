package io.github.betterclient.ascendium.module.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Bridge
import io.github.betterclient.ascendium.IdentifierBridge
import io.github.betterclient.ascendium.ItemStackBridge
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.HUDModule
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

object ArmorDisplayMod : HUDModule("Armor display", "Display your armor") {
    var helmet: ItemStackBridge? by mutableStateOf(null)
    var chestplate: ItemStackBridge? by mutableStateOf(null)
    var leggings: ItemStackBridge? by mutableStateOf(null)
    var boots: ItemStackBridge? by mutableStateOf(null)
    override val renderBackground: Boolean by boolean("Together", false)
    val orientation by dropdown("Orientation", "Horizontal", "Horizontal", "Vertical")

    @Composable
    override fun Render() {
        if (orientation == "Horizontal") {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RenderItems()
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RenderItems()
            }
        }
    }

    @Composable
    private fun RenderItems() {
        RenderItem(helmet)
        RenderItem(chestplate)
        RenderItem(leggings)
        RenderItem(boots)
    }

    @Composable
    private fun RenderItem(item: ItemStackBridge?) {
        remember(item?.itemIdentifier) {
            item?.itemIdentifier?.let { id ->
                Bridge.client.loadResource(
                    IdentifierBridge(id.namespace, "textures/item/${id.path}.png")
                )?.let { resource ->
                    ImageIO.read(ByteArrayInputStream(resource)).toComposeImageBitmap()
                }
            }
        }?.let {
            if (!renderBackground) {
                Box(
                    Modifier
                        .background(
                            Color(backgroundColor.state.value),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                ) {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        filterQuality = FilterQuality.None
                    )
                }
            } else {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp),
                    filterQuality = FilterQuality.None
                )
            }
        }
    }

    @EventTarget
    fun onRender(render: RenderHudEvent) {
        helmet = Bridge.client.player.getArmor(3)
        chestplate = Bridge.client.player.getArmor(2)
        leggings = Bridge.client.player.getArmor(1)
        boots = Bridge.client.player.getArmor(0)
    }
}