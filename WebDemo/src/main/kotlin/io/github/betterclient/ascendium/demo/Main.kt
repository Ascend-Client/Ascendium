package io.github.betterclient.ascendium.demo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.betterclient.ascendium.demo.ui.MainMenu
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    document.addEventListener("DOMContentLoaded", {
        try {
            CoroutineScope(Dispatchers.Default).launch {
                onWasmReady {
                    ComposeViewport(document.body!!) {
                        MainMenu()
                    }

                    document.getElementById("downloader")!!.remove()
                }
            }
        } catch (_: dynamic) {
            window.location.reload()
        }
    })
}