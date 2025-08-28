package io.github.betterclient.ascendium.module.impl

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.event.ChatEvent
import io.github.betterclient.ascendium.event.EventTarget
import io.github.betterclient.ascendium.event.RenderHudEvent
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.module.Module
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import kotlinx.coroutines.delay

object Notifications : Module("Notifications", "Show notifications for in-game events on supported servers") {
    @OptIn(InternalComposeUiApi::class)
    lateinit var scene: ComposeScene

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onRender(event: RenderHudEvent) {
        init()
        SkiaRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }
    }

    @OptIn(InternalComposeUiApi::class)
    private fun init() {
        val window = minecraft.window
        if (!::scene.isInitialized) {
            val density = Density(1f)
            scene = CanvasLayersComposeScene(
                density = density,
                size = IntSize(window.fbWidth, window.fbHeight),
                invalidate = {/*Minecraft should schedule?*/}
            )

            scene.setContent {
                RenderNotificationsHud()
            }
        } else {
            if (window.fbWidth != scene.size!!.width || window.fbHeight != scene.size!!.height) {
                scene.size = IntSize(window.fbWidth, window.fbHeight)
            }
        }
    }
    val notifications = mutableStateMapOf<Long, Notification>()

    @Composable
    private fun RenderNotificationsHud() {
        AscendiumTheme {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                Column {
                    Spacer(Modifier.height(10.dp))
                    var latest by remember {
                        mutableStateOf(notifications
                            .filterKeys { System.currentTimeMillis() - it < 5000 }
                            .maxByOrNull { it.key }
                            ?.value)
                    }
                    LaunchedEffect(Unit) {
                        while (true) {
                            latest = notifications
                                .filterKeys { System.currentTimeMillis() - it < 5000 }
                                .maxByOrNull { it.key }
                                ?.value
                            delay(500)
                        }
                    }

                    latest?.let { RenderNotification(it) }
                }
            }
        }
    }

    @Composable
    fun RenderNotification(notification: Notification) {
        var a by remember { mutableStateOf(false) }
        LaunchedEffect(notification, Unit) {
            a = true
        }
        val offset by animateDpAsState(
            if (a) (-10).dp else 260.dp
        )

        Box(
            Modifier
                .offset(offset)
                .background(
                    AscendiumTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat()),
                    RoundedCornerShape(16.dp)
                )
                .size(250.dp, 150.dp)
        ) {
            IconButton(
                onClick = {
                    notifications.remove(notifications.filterValues { it == notification }.keys.min()) //ew hack
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = notification.bigTitle,
                    style = AscendiumTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.smallText,
                    style = AscendiumTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                NotificationButtons(notification)
            }
        }
    }

    @Composable
    private fun NotificationButtons(notification: Notification) {
        if (notification.button1 != NotificationButton.None) {
            if (notification.button2 != NotificationButton.None) {
                //combined button
                Row(Modifier.fillMaxWidth()) {
                    Button(
                        notification.button1.onClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = notification.button1.color),
                        shape = RoundedCornerShape(
                            topStartPercent = 50,
                            bottomStartPercent = 50,
                            topEndPercent = 10,
                            bottomEndPercent = 10
                        )
                    ) {
                        Text(notification.button1.text)
                    }
                    Spacer(Modifier.width(2.dp))
                    Button(
                        notification.button2.onClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = notification.button2.color),
                        shape = RoundedCornerShape(
                            topStartPercent = 10,
                            bottomStartPercent = 10,
                            topEndPercent = 50,
                            bottomEndPercent = 50
                        )
                    ) {
                        Text(notification.button2.text)
                    }
                }
            } else {
                Button(
                    notification.button1.onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = notification.button1.color)
                ) {
                    Text(notification.button1.text)
                }
            }
        }
    }

    @EventTarget
    fun onChat(chatEvent: ChatEvent) {
        notifications[System.currentTimeMillis()] = Notification(chatEvent.text.text, chatEvent.text.style.toString())
    }
}

data class Notification(
    val bigTitle: String,
    val smallText: String,
    val button1: NotificationButton = NotificationButton.None,
    val button2: NotificationButton = NotificationButton.None
)

data class NotificationButton(
    val text: String,
    val onClick: () -> Unit,
    val color: Color = Color.Unspecified,
) {
    companion object {
        val None = NotificationButton("", {})
    }
}