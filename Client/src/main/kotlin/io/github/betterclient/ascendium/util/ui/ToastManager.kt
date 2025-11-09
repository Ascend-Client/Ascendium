package io.github.betterclient.ascendium.util.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.ui.bridge.ComposeUI

fun showToast(text: String) {
    ComposeUI.current.toast {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val no = false
            val yes0 = remember { mutableStateOf(no) }
            var yes by yes0
            LaunchedEffect(Unit) {
                yes = true
            }

            AnimatedVisibility(
                visible = yes,
            ) {
                Column(
                    Modifier
                        .detectOutsideClick(yes0) {
                            yes = no //hehe
                            Thread {
                                Thread.sleep(300)
                                ComposeUI.current.toast {  }
                            }.start()
                        }
                        .background(darkColorScheme().primaryContainer, RoundedCornerShape(16.dp)).padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = text,
                        color = AscendiumTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = {
                            yes = no
                            Thread {
                                Thread.sleep(300)
                                ComposeUI.current.toast {  }
                            }.start()
                        }
                    ) { Text("OK") }
                }
            }
        }
    }
}