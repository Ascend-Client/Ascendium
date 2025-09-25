package io.github.betterclient.ascendium.module.impl.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.module.impl.notifications.Notifications.notifications
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme

@Composable
internal fun RenderNotification(notification: Notification, onDelete: () -> Unit = {}) {
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