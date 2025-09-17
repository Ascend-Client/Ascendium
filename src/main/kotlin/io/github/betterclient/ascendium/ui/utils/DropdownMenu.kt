package io.github.betterclient.ascendium.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.min

//the default material3 dropdown menu ONLY WORKS ON ANDROID
@Composable
fun DropdownMenu(
    modifier: Modifier = Modifier,
    theme: ColorScheme,
    options: List<String>,
    selectedOption: MutableState<String>,
    onOptionSelected: (String) -> Unit
) {
    var curOption by remember { mutableStateOf(selectedOption) }
    val expanded0 = remember { mutableStateOf(false) }
    var expanded by expanded0

    Box(modifier = modifier.detectOutsideClick(expanded0) { expanded = false }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = curOption.value,
                onValueChange = {},
                readOnly = true,
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