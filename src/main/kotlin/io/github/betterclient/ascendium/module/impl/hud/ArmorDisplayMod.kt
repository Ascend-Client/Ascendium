package io.github.betterclient.ascendium.module.impl.hud

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.betterclient.ascendium.IdentifierBridge
import io.github.betterclient.ascendium.ItemStackBridge
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.module.HUDModule
import io.github.betterclient.ascendium.ui.config.rgb
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

object ArmorDisplayMod : HUDModule("Armor display", "Display your armor") {
    var helmet: ItemStackBridge? by mutableStateOf(null)
    var chestplate: ItemStackBridge? by mutableStateOf(null)
    var leggings: ItemStackBridge? by mutableStateOf(null)
    var boots: ItemStackBridge? by mutableStateOf(null)
    var heldItem: ItemStackBridge? by mutableStateOf(null)

    override val renderBackground by boolean("Together", false)
    val orientation by dropdown("Orientation", "Horizontal", "Horizontal", "Vertical")
    val displayCount by boolean("Item counts", true)
    val displayDurability by boolean("Item durability", true)
    val maxDur by color("High durability", Color.Green.rgb)
    val midDur by color("Mid durability", Color.Yellow.rgb)
    val minDur by color("Low durability", Color.Red.rgb)
    val durBG by color("Durability background", Color.Gray.rgb)

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
    override fun RenderPreview() {
        if (orientation == "Horizontal") {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                RenderItemPreview()
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                RenderItemPreview()
            }
        }
    }

    @Composable
    private fun RenderItems() {
        RenderItem(helmet)
        RenderItem(chestplate)
        RenderItem(leggings)
        RenderItem(boots)
        RenderItem(heldItem)
    }

    @Composable
    private fun RenderItemPreview() {
        RenderItem(minecraft.createItemStack(
            IdentifierBridge("minecraft", "textures/item/netherite_helmet.png"), 1, 1f
        ))
        RenderItem(minecraft.createItemStack(
            IdentifierBridge("minecraft", "textures/item/netherite_chestplate.png"), 1, 0.9f
        ))
        RenderItem(minecraft.createItemStack(
            IdentifierBridge("minecraft", "textures/item/netherite_leggings.png"), 1, 0.6f
        ))
        RenderItem(minecraft.createItemStack(
            IdentifierBridge("minecraft", "textures/item/netherite_boots.png"), 1, 0.2f
        ))
        RenderItem(minecraft.createItemStack(
            IdentifierBridge("minecraft", "textures/item/golden_apple.png"), 64, 1f
        ))
    }

    @Composable
    private fun RenderItem(item: ItemStackBridge?) {
        remember(item?.itemIdentifier) {
            item?.itemIdentifier?.let { id ->
                minecraft.loadResource(id)?.let {
                    ImageIO.read(ByteArrayInputStream(it)).toComposeImageBitmap()
                }
            }
        }?.let {
            RenderItemBitmap(item, it)
        }
    }

    @Composable
    private fun RenderItemBitmap(item: ItemStackBridge?, bitmap: ImageBitmap) {
        @Composable fun CImage() {
            Box {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp),
                    filterQuality = FilterQuality.None
                )

                if (displayCount && (item?.itemCount ?: 0) > 1) {
                    Text(
                        text = "${item?.itemCount}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(1.dp)
                            .offset(15.dp, 10.dp)
                    )
                }
            }
        }

        Box(
            Modifier
                .then(if (renderBackground) Modifier else {
                    Modifier
                        .background(
                            Color(backgroundColor.state.value),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp)
                })
        ) {
            val durabilityColor by animateColorAsState(
                if ((item?.durability ?: 1f) > 0.7f) {
                    Color(maxDur)
                } else if ((item?.durability?: 1f) > 0.4) {
                    Color(midDur)
                } else {
                    Color(minDur)
                }
            )

            //Reverse orientation for durability and item count
            if (orientation == "Horizontal") {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CImage()
                    if (displayDurability && item?.durability != 1f) {
                        Box(
                            Modifier
                                .width(25.dp)
                                .height(2.dp)
                                .background(Color(durBG))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(item!!.durability)
                                    .height(2.dp)
                                    .background(durabilityColor)
                            )
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CImage()
                    if (displayDurability && item?.durability != 1f) {
                        Box(
                            Modifier
                                .height(25.dp)
                                .width(2.dp)
                                .background(Color(durBG))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxHeight(item!!.durability)
                                    .width(2.dp)
                                    .background(durabilityColor)
                            )
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    fun onRender(render: RenderHudEvent) {
        helmet = minecraft.player.getArmor(3)
        chestplate = minecraft.player.getArmor(2)
        leggings = minecraft.player.getArmor(1)
        boots = minecraft.player.getArmor(0)
        heldItem = minecraft.player.getMainHandItem()
    }
}