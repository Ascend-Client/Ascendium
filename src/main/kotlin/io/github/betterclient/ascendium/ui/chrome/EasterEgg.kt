package io.github.betterclient.ascendium.ui.chrome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.compose.ComposeUI
import io.github.betterclient.ascendium.ui.mods.ModsUI
import io.github.betterclient.ascendium.ui.utils.AscendiumTheme

val browser = Browser(ChromiumDownloader.client!!, "https://google.com/")

@Composable
fun EasterEggUI() {
    AscendiumTheme {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.align(Alignment.Center)) {
                Row(
                    Modifier
                        .size(800.dp, 100.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = Ascendium.settings.backgroundOpacityState.toFloat()),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Navigation()
                }

                BrowserView(
                    browser,
                    modifier = Modifier.size(800.dp, 600.dp),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun RowScope.Navigation() {
    var textFieldValue by remember(browser.myURL) { mutableStateOf(browser.myURL) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Spacer(Modifier.width(2.dp))

    Button(onClick = {
        ComposeUI.current.switchTo { ModsUI(true) }
    }) { Text("Back") }

    IconButton(
        onClick = { browser.back() },
        enabled = browser.canGoBack
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }

    IconButton(
        onClick = { browser.forward() },
        enabled = browser.canGoForward
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Forward",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }

    TextField(
        value = textFieldValue,
        onValueChange = { textFieldValue = it },
        modifier = Modifier.weight(1f),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = {
            browser.setUrl(textFieldValue)
            keyboardController?.hide()
        })
    )

    IconButton(onClick = {
        browser.setUrl("https://www.google.com")
    }) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
    }

    Spacer(Modifier.width(2.dp))
}