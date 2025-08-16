package io.github.betterclient.ascendium.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.betterclient.ascendium.Ascendium
import io.github.betterclient.ascendium.BridgeRenderer
import org.jetbrains.skia.IRect
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import kotlin.math.min

@Composable
fun Modifier.detectOutsideClick(
    onOutsideClick: () -> Unit
): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }
    LaunchedEffect(Unit) {
        fun handler(composeCoords: Offset, mcMouse: Offset, button: Int, clicked: Boolean): Boolean {
            if (position == null) return false
            if (button != 0) return false

            if (composeCoords.x < position!!.left || composeCoords.x > position!!.right ||
                composeCoords.y < position!!.top || composeCoords.y > position!!.bottom) {
                onOutsideClick()
                ComposeUI.current.removeMouseHandler(::handler)
                return true
            }

            return false
        }
        ComposeUI.current.addMouseHandler(::handler)
    }

    return this.onGloballyPositioned { positions ->
        position = positions.toRect()
    }
}

private fun LayoutCoordinates.toRect() = Rect(
    left = this.positionInRoot().x,
    top = this.positionInRoot().y,
    right = this.positionInRoot().x + this.size.width,
    bottom = this.positionInRoot().y + this.size.height
)

@Composable
private fun colorScheme() = when(Ascendium.settings.themeState) {
    "Dark" -> darkColorScheme().setButtonColors()
    "Light" -> lightColorScheme().setButtonColors()
    "Minecraft" -> mcColorScheme
    "Diamond" -> diamondColorScheme
    else -> throw IllegalStateException()
}

private fun ColorScheme.setButtonColors() = this.copy(primary = primaryContainer, onPrimary = onPrimaryContainer)

fun showToast(text: String) {
    ComposeUI.current.toast {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val no = false
            var yes by remember { mutableStateOf(no) }
            LaunchedEffect(Unit) {
                yes = true
            }

            AnimatedVisibility(
                visible = yes,
            ) {
                Column(
                    Modifier
                        .detectOutsideClick {
                            yes = no //hehe
                            Thread {
                                Thread.sleep(300)
                                ComposeUI.current.toast {  }
                            }.start()
                        }
                        .background(colorScheme().primaryContainer, RoundedCornerShape(16.dp)).padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onBackground
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

@Composable
fun AscendiumTheme(content: @Composable () -> Unit) {
    val colorScheme = colorScheme()
    val t = Typography(
        displayLarge = TextStyle(color = colorScheme.onBackground),
        displayMedium = TextStyle(color = colorScheme.onBackground),
        displaySmall = TextStyle(color = colorScheme.onBackground),
        headlineLarge = TextStyle(color = colorScheme.onBackground),
        headlineMedium = TextStyle(color = colorScheme.onBackground),
        headlineSmall = TextStyle(color = colorScheme.onBackground),
        titleLarge = TextStyle(color = colorScheme.onBackground),
        titleMedium = TextStyle(color = colorScheme.onBackground),
        titleSmall = TextStyle(color = colorScheme.onBackground),
        bodyLarge = TextStyle(color = colorScheme.onBackground),
        bodyMedium = TextStyle(color = colorScheme.onBackground),
        bodySmall = TextStyle(color = colorScheme.onBackground),
        labelLarge = TextStyle(color = colorScheme.onBackground),
        labelMedium = TextStyle(color = colorScheme.onBackground),
        labelSmall = TextStyle(color = colorScheme.onBackground),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
        typography = t
    )
}

@Composable
fun rainbowAsState(): State<Color> {
    val infiniteTransition = rememberInfiniteTransition(label = "rainbow")

    val hue = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hue"
    )

    return derivedStateOf {
        Color.hsv(hue.value, 1f, 1f)
    }
}

@Composable
fun Center(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
fun Modifier.renderWithMC(visible: State<Boolean>, block: (context: BridgeRenderer, coords: IRect) -> Unit): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }

    LaunchedEffect(Unit) {
        ComposeUI.current.addRenderHandler { context, mouseX, mouseY ->
            if (position == null) return@addRenderHandler
            if (!visible.value) return@addRenderHandler

            block(context, IRect.makeLTRB(
                position!!.left.getScaled().toInt(),
                position!!.top.getScaled().toInt(),
                position!!.right.getScaled().toInt(),
                position!!.bottom.getScaled().toInt()
            ))
        }
    }

    return this.onGloballyPositioned {
        position = it.toRect()
    }
}

//the default material3 dropdown menu ONLY WORKS ON ANDROID
@Composable
fun DropdownMenu(
    modifier: Modifier = Modifier,
    theme: ColorScheme,
    options: List<String>,
    selectedOption: MutableState<String>,
    onOptionSelected: (String) -> Unit,
    name: String
) {
    var curOption by remember { mutableStateOf(selectedOption) }
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.detectOutsideClick {
        expanded = false
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = curOption.value,
                onValueChange = {},
                readOnly = true,
                label = { Text(name) },
                trailingIcon = { TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = theme.onSurface, unfocusedTextColor = theme.onSurface, disabledTextColor = theme.onSurface,
                    focusedContainerColor = theme.surface, unfocusedContainerColor = theme.surface, disabledContainerColor = theme.surface,
                    focusedIndicatorColor = theme.primary, unfocusedIndicatorColor = theme.outline, disabledIndicatorColor = theme.outline,
                    focusedLabelColor = theme.primary, unfocusedLabelColor = theme.onSurfaceVariant, disabledLabelColor = theme.onSurfaceVariant,
                    disabledTrailingIconColor = theme.onSurfaceVariant, //BULLSHIT
                )
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
            modifier = Modifier.padding(top = 65.dp)
        ) {
            val scrollState = rememberLazyListState()
            Box(Modifier
                .fillMaxWidth()
                .height((min(options.size, 5) * 30).dp)
                .background(color = theme.background)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = scrollState
                ) {
                    items(options) { option ->
                        Box(Modifier.fillMaxWidth().height(30.dp).clickable {
                            onOptionSelected(option)
                            curOption.value = option
                            expanded = false
                        }) {
                            Text(option)
                        }
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(8.dp)
                )
            }
        }
    }
}

@Composable
fun TrailingIcon(expanded: Boolean) {
    //Copied from ExposedDropdownMenuDefaults.TrailingIcon
    Icon(Icons.Default.ArrowDropDown, null, Modifier.rotate(if (expanded) 180f else 0f))
}

@Composable
fun Modifier.detectInsideEvent(component: Component, func: (mouseX: Int, mouseY: Int, event: MouseEvent) -> Unit): Modifier {
    var position by remember { mutableStateOf<Rect?>(null) }
    LaunchedEffect(Unit) {
        ComposeUI.current.addMouseEventHandler { x, y, event ->
            if (position == null) return@addMouseEventHandler false

            if (x < position!!.left || x > position!!.right ||
                y < position!!.top || y > position!!.bottom) {
                return@addMouseEventHandler false //outside
            }

            func(
                (x - position!!.left).toInt(),
                (y - position!!.top).toInt(),
                translateMouseEvent(component, position!!.left.toInt(), position!!.top.toInt(), event)
            )
            return@addMouseEventHandler false
        }
    }
    return this.onGloballyPositioned {
        position = it.toRect()
    }
}

private fun translateMouseEvent(
    component: Component,
    x: Int,
    y: Int,
    event: MouseEvent
): MouseEvent {
    val translatedX = event.x - x
    val translatedY = event.y - y

    return when (event) {
        is MouseWheelEvent -> MouseWheelEvent(
            component,
            event.id,
            event.`when`,
            event.modifiersEx,
            translatedX,
            translatedY,
            event.clickCount,
            event.isPopupTrigger,
            event.scrollType,
            event.scrollAmount,
            event.wheelRotation
        )
        else -> MouseEvent(
            component,
            event.id,
            event.`when`,
            event.modifiersEx,
            translatedX,
            translatedY,
            event.clickCount,
            event.isPopupTrigger,
            event.button
        )
    }
}

private val mcColorScheme = ColorScheme(
    primary = Color(0xFF6A9B54),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF456B37),
    onPrimaryContainer = Color(0xFFB3E0A3),
    inversePrimary = Color(0xFF6A9B54),
    secondary = Color(0xFF8D9296),
    onSecondary = Color(0xFF1E2122),
    secondaryContainer = Color(0xFF42474A),
    onSecondaryContainer = Color(0xFFDCE2E6),
    tertiary = Color(0xFF68D1E3),
    onTertiary = Color(0xFF00252A),
    tertiaryContainer = Color(0xFF004D58),
    onTertiaryContainer = Color(0xFFB9F2FF),
    background = Color(0xFF1A1C19),
    onBackground = Color(0xFFE2E3DE),
    surface = Color(0xFF1A1C19),
    onSurface = Color(0xFFE2E3DE),
    surfaceVariant = Color(0xFF414940),
    onSurfaceVariant = Color(0xFFC1C9BE),
    surfaceTint = Color(0xFF6A9B54),
    inverseSurface = Color(0xFFE2E3DE),
    inverseOnSurface = Color(0xFF1A1C19),
    error = Color(0xFFC53434),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7A2020),
    onErrorContainer = Color(0xFFF9DADA),
    outline = Color(0xFF8B9389),
    outlineVariant = Color(0xFF414940),
    scrim = Color(0x99000000),
    surfaceDim = Color(0xFF121411),
    surfaceBright = Color(0xFF383A36),
    surfaceContainerLowest = Color(0xFF151714),
    surfaceContainerLow = Color(0xFF1A1C19),
    surfaceContainer = Color(0xFF1E201D),
    surfaceContainerHigh = Color(0xFF282B27),
    surfaceContainerHighest = Color(0xFF333632),
)

private val diamondColorScheme = ColorScheme(
    primary = Color(0xFF6C99FF),
    onPrimary = Color(0xFF001D53),
    primaryContainer = Color(0xFF2E468A),
    onPrimaryContainer = Color(0xFFD9E2FF),
    inversePrimary = Color(0xFF4C61A1),
    secondary = Color(0xFF8A9296),
    onSecondary = Color(0xFF1E2122),
    secondaryContainer = Color(0xFF42474A),
    onSecondaryContainer = Color(0xFFDCE2E6),
    tertiary = Color(0xFFDDB0FF),
    onTertiary = Color(0xFF432258),
    tertiaryContainer = Color(0xFF5C3A70),
    onTertiaryContainer = Color(0xFFF0D9FF),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF42474C),
    onSurfaceVariant = Color(0xFFC2C7CD),
    surfaceTint = Color(0xFF6C99FF),
    inverseSurface = Color(0xFFE2E2E6),
    inverseOnSurface = Color(0xFF1A1C1E),
    error = Color(0xFFC53434),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7A2020),
    onErrorContainer = Color(0xFFF9DADA),
    outline = Color(0xFF8C9198),
    outlineVariant = Color(0xFF42474C),
    scrim = Color(0x99000000),
    surfaceDim = Color(0xFF111415),
    surfaceBright = Color(0xFF373A3C),
    surfaceContainerLowest = Color(0xFF151718),
    surfaceContainerLow = Color(0xFF1A1C1E),
    surfaceContainer = Color(0xFF1E2022),
    surfaceContainerHigh = Color(0xFF282A2C),
    surfaceContainerHighest = Color(0xFF333537),
)