package io.github.betterclient.ascendium.module.impl

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeCanvas
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.scene.CanvasLayersComposeScene
import androidx.compose.ui.scene.ComposeScene
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.AWTUtils
import io.github.betterclient.ascendium.compose.SkiaRenderer
import io.github.betterclient.ascendium.compose.glfwToAwtKeyCode
import io.github.betterclient.ascendium.event.*
import io.github.betterclient.ascendium.minecraft
import io.github.betterclient.ascendium.module.Module
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import kotlinx.coroutines.delay
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

object Notifications : Module("Notifications", "Show notifications for in-game events on supported servers") {
    @OptIn(InternalComposeUiApi::class)
    lateinit var scene: ComposeScene

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
    var multipleNotifications by mutableStateOf(false)

    @Composable
    private fun RenderNotificationsHud() {
        AscendiumTheme {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(10.dp))
                    AnimatedContent(
                        targetState = multipleNotifications,
                        transitionSpec = {
                            expandVertically() + fadeIn() togetherWith shrinkVertically() + fadeOut()
                        }
                    ) { it ->
                        if (it) {
                            MultipleNotifications()
                        } else {
                            SingleNotification()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MultipleNotifications() = Column(Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.End) {
        var sortedNotifications by remember { mutableStateOf(
            notifications.toList()
                .sortedByDescending { it.first }
                .map { it.second }
        ) }
        var update by remember { mutableStateOf(false) }
        LaunchedEffect(update) {
            sortedNotifications = notifications.toList()
                .sortedByDescending { it.first }
                .map { it.second }
        }

        if (sortedNotifications.isEmpty()) return@Column

        var searchBar by remember { mutableStateOf("") }
        Row(Modifier.width(IntrinsicSize.Min)) {
            OutlinedTextField(
                searchBar,
                onValueChange = {
                    searchBar = it
                    if (searchBar.isNotEmpty()) {
                        sortedNotifications = searchNotifications(searchBar)
                    } else {
                        update = !update
                    }
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(2.dp))
            Button(onClick = {
                notifications.clear()
                sortedNotifications = notifications.toList()
                    .sortedByDescending { it.first }
                    .map { it.second }
            }, modifier = Modifier.weight(1f)) {
                Text("Clear All")
            }
        }
        Spacer(Modifier.height(10.dp))

        sortedNotifications.forEach { notification ->
            key(notification) {
                RenderNotification(notification) {
                    update = !update
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    private fun searchNotifications(query: String): List<Notification> {
        return notifications.values
            .filter { it.bigTitle.contains(query, ignoreCase = true) }
            .sortedByDescending { notifications.entries.first { entry -> entry.value == it }.key }
    }

    @Composable
    private fun SingleNotification() {
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

        Box(Modifier.offset((-10).dp)) {
            AnimatedContent(
                targetState = latest,
                transitionSpec = {
                    slideInHorizontally { -it / 4 } togetherWith slideOutHorizontally { it * 4 }
                }
            ) { notif ->
                notif?.let { RenderNotification(it) }
            }
        }
    }

    @Composable
    private fun RenderNotification(notification: Notification, onDelete: () -> Unit = {}) {
        Box(
            Modifier
                .background(
                    AscendiumTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat()),
                    RoundedCornerShape(16.dp)
                )
                .size(250.dp, 150.dp)
        ) {
            IconButton(
                onClick = {
                    onDelete()
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

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onRender(event: RenderHudEvent) {
        multipleNotifications = minecraft.isScreenNull.not()
        init()
        SkiaRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }
    }

    @EventTarget
    fun onChat(chatEvent: ChatEvent) {
        notifications[System.currentTimeMillis()] = Notification(chatEvent.text.text, chatEvent.text.style.toString())
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onScreenRender(event: RenderScreenEvent) {
        if (minecraft.isWorldNull) return
        init()
        SkiaRenderer.withSkia {
            scene.render(it.asComposeCanvas(), System.nanoTime())
        }

        val event = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(minecraft.window.windowHandle),
            0, //NO BUTTON
            MouseEvent.MOUSE_MOVED
        )
        scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = PointerEventType.Move,
            nativeEvent = event
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onScreenMouse(event: MouseScreenEvent) {
        if (minecraft.isWorldNull) return
        init()
        val event0 = AWTUtils.MouseEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            AWTUtils.getAwtMods(minecraft.window.windowHandle),
            event.button,
            if (event.pressed) MouseEvent.MOUSE_PRESSED else MouseEvent.MOUSE_RELEASED
        )

        scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = if (event.pressed) PointerEventType.Press else PointerEventType.Release,
            nativeEvent = event0,
            button = PointerButton(event.button)
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onScreenScroll(event: MouseScrollScreenEvent) {
        if (minecraft.isWorldNull) return
        init()
        val event0 = AWTUtils.MouseWheelEvent(
            minecraft.mouse.xPos,
            minecraft.mouse.yPos,
            event.scrollY,
            AWTUtils.getAwtMods(minecraft.window.windowHandle),
            MouseEvent.MOUSE_WHEEL
        )

        scene.sendPointerEvent(
            position = Offset(minecraft.mouse.xPos.toFloat(), minecraft.mouse.yPos.toFloat()),
            eventType = PointerEventType.Scroll,
            scrollDelta = Offset(event.scrollX.toFloat(), -event.scrollY.toFloat()),
            nativeEvent = event0
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onKeyboardScreen(event: KeyboardScreenEvent) {
        if (minecraft.isWorldNull) return
        init()
        val awtKey = glfwToAwtKeyCode(event.key)
        val time = System.nanoTime() / 1_000_000

        scene.sendKeyEvent(
            AWTUtils.KeyEvent(
                if (event.pressed) KeyEvent.KEY_PRESSED else KeyEvent.KEY_RELEASED,
                time,
                AWTUtils.getAwtMods(minecraft.window.windowHandle),
                awtKey,
                0.toChar(),
                KeyEvent.KEY_LOCATION_STANDARD
            )
        )
    }

    @OptIn(InternalComposeUiApi::class)
    @EventTarget
    fun onKeyboardChar(event: KeyboardCharScreenEvent) {
        if (minecraft.isWorldNull) return
        init()
        val time = System.nanoTime() / 1_000_000
        scene.sendKeyEvent(
            AWTUtils.KeyEvent(
                KeyEvent.KEY_TYPED,
                time,
                AWTUtils.getAwtMods(minecraft.window.windowHandle),
                Key.Unknown.keyCode.toInt(),
                event.char,
                KeyEvent.KEY_LOCATION_UNKNOWN
            )
        )
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