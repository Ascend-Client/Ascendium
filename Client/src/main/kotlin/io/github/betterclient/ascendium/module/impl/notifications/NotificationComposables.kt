package io.github.betterclient.ascendium.module.impl.notifications

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.module.impl.notifications.Notifications.multipleNotifications
import io.github.betterclient.ascendium.module.impl.notifications.Notifications.notifications
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme
import kotlinx.coroutines.delay

@Composable
internal fun RenderNotificationsHud() {
    AscendiumTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
            Column(horizontalAlignment = Alignment.End) {
                Spacer(Modifier.height(10.dp))
                AnimatedVisibility(multipleNotifications, enter = expandVertically(), exit = shrinkVertically()) {
                    MultipleNotifications()
                }
                AnimatedVisibility(!multipleNotifications, enter = expandVertically(), exit = shrinkVertically()) {
                    SingleNotification()
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

    var searchBar by remember { mutableStateOf("") }
    if (notifications.isEmpty()) return@Column

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
            Box(Modifier.offset((-10).dp)) {
                RenderNotification(notification) {
                    update = !update
                }
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