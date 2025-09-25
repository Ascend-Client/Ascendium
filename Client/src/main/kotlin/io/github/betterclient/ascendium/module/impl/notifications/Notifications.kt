package io.github.betterclient.ascendium.module.impl.notifications

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import io.github.betterclient.ascendium.event.eventBus
import io.github.betterclient.ascendium.module.Module

object Notifications : Module("Notifications", "Show notifications for in-game events on supported servers") {
    val notifications = mutableStateMapOf<Long, Notification>()
    var multipleNotifications by mutableStateOf(false)

    override fun toggle() {
        super.toggle()
        if (enabled) {
            NotificationEvents.eventBus.subscribe()
        } else {
            NotificationEvents.eventBus.unsubscribe()
        }
    }

    fun addNotification(notification: Notification) {
        notifications[System.currentTimeMillis()] = notification
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