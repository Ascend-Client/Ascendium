package io.github.betterclient.ascendium.screen.mods

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.detectOutsideClick
import io.github.betterclient.ascendium.module.config.ConfigManager
import java.io.File

@Composable
fun ConfigTab() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val state = rememberScrollState()
        Column(
            Modifier
                .height(400.dp)
                .fillMaxSize(0.7f)
                .verticalScroll(state),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConfigList()
            NewConfigButton()
        }
    }
}

@Composable
private fun NewConfigButton() {
    var showCreate by remember { mutableStateOf(false) }
    IconButton(
        onClick = {
            showCreate = !showCreate
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.Green,
            modifier = Modifier.size(20.dp)
        )
    }

    AnimatedVisibility(
        visible = showCreate,
        modifier = Modifier
            .size(100.dp, 50.dp)
            .detectOutsideClick { showCreate = false }
    ) {
        Column(
            Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
            .fillMaxSize()
        ) {
            var name by remember { mutableStateOf("") }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Config Name") },
                modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (name.isNotBlank()) {
                            ConfigManager.activeConfig = name
                            ConfigManager.saveConfig()
                            name = ""
                            showCreate = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showCreate = false }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigList() {
    var configs = remember { File(".ascendium").listFiles()?.filter { it.isDirectory }?.map { it.name }?:
        ConfigManager.saveConfig().let { listOf(ConfigManager.activeConfig) } } //this should create a config

    var active by remember { mutableStateOf(ConfigManager.activeConfig) }

    configs.forEach { config ->
        Box(
            Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = {
                        ConfigManager.loadConfig(config)
                        active = config
                    })
                }
                .height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                config,
                modifier = Modifier.align(Alignment.Center),
                color = if (config == active) {
                    Color.Green
                } else {
                    Color.White
                }
            )

            IconButton(
                onClick = {
                    if (configs.size <= 1) return@IconButton
                    val file = File(".ascendium", config)
                    file.deleteRecursively()

                    configs = File(".ascendium").listFiles()?.filter { it.isDirectory }?.map { it.name }?:
                            ConfigManager.saveConfig().let { listOf(ConfigManager.activeConfig) } //this should create a config

                    if (config == active) {
                        active = configs.firstOrNull() ?: ConfigManager.activeConfig
                        ConfigManager.loadConfig(active)
                    }
                },
                modifier = Modifier.size(20.dp).align(Alignment.CenterEnd),
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}