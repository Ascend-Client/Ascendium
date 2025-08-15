package io.github.betterclient.ascendium.ui.mods

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.compose.Center
import io.github.betterclient.ascendium.compose.detectOutsideClick
import io.github.betterclient.ascendium.compose.showToast
import io.github.betterclient.ascendium.module.config.ConfigManager
import java.io.File

@Composable
fun ConfigTab() {
    Center {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val configs = remember { mutableStateOf(
                File(".ascendium").listFiles()?.filter { it.isDirectory }?.map { it.name }?: //list directories
                ConfigManager.saveConfig().let { listOf(ConfigManager.activeConfig) } //this should create a config (none were found) (hack, should only happen if the user deletes the folders)
            ) }
            ConfigList(configs)
            NewConfigButton(configs)
        }
    }
}

@Composable
private fun NewConfigButton(configs1: MutableState<List<String>>) {
    var showCreate by remember { mutableStateOf(false) }
    val corner by animateDpAsState(targetValue = if (showCreate) 16.dp else 0.dp)
    AnimatedContent(
        showCreate
    ) {
        if (showCreate) {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(corner))
                    .size(300.dp, 120.dp).detectOutsideClick { showCreate = false },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                var name by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Config Name") },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = OutlinedTextFieldDefaults
                        .colors()
                        .copy(
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer //make the container visible while unfocused
                        )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (name.isNotBlank()) {
                                ConfigManager.activeConfig = name
                                ConfigManager.saveConfig()
                                name = ""
                                showCreate = false

                                //force recompose
                                configs1.value = File(".ascendium").listFiles()?.filter { it.isDirectory }?.map { it.name }?:
                                        ConfigManager.saveConfig().let { listOf(ConfigManager.activeConfig) } //this should create a config
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
        } else {
            IconButton(
                onClick = {
                    showCreate = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ConfigList(configs0: MutableState<List<String>>) {
    var configs by configs0

    var active by remember { mutableStateOf(ConfigManager.activeConfig) }

    LaunchedEffect(configs) {
        active = ConfigManager.activeConfig
    }

    val state = rememberScrollState()
    Column(Modifier
        .background(
            MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(16.dp)
        )
        .requiredHeightIn(max = 300.dp)
        .fillMaxWidth(0.7f)
        .verticalScroll(state)
    ) {
        configs.forEach { config ->
            key(config) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                ConfigManager.saveConfig()
                                ConfigManager.loadConfig(config)
                                active = config
                            })
                        }
                        .height(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        config,
                        modifier = Modifier.align(Alignment.Center),
                        color = if (config == active) {
                            Color.Green
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )

                    IconButton(
                        onClick = {
                            if (configs.size <= 1) {
                                showToast("You must have at least one config")
                                return@IconButton
                            }
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
    }
}