package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun InlineTaskTitle(
    title: String,
    style: TextStyle,
    requestFocus: Boolean,
    onTitleChange: (String) -> Unit,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onFocusHandled: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isEditing by remember { mutableStateOf(false) }
    var pendingFocusRequest by remember { mutableStateOf(false) }
    var fieldValue by remember(title) {
        mutableStateOf(
            TextFieldValue(
                text = title,
                selection = TextRange(title.length)
            )
        )
    }

    LaunchedEffect(title) {
        if (!isEditing && title != fieldValue.text) {
            fieldValue = TextFieldValue(
                text = title,
                selection = TextRange(title.length)
            )
        }
    }

    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            fieldValue = TextFieldValue(
                text = title,
                selection = TextRange(title.length)
            )
            isEditing = true
            pendingFocusRequest = true
            onFocusHandled()
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            BasicTextField(
                value = fieldValue,
                onValueChange = { newValue ->
                    fieldValue = newValue
                    if (newValue.text != title) {
                        onTitleChange(newValue.text)
                    }
                },
                singleLine = true,
                textStyle = style,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    if (fieldValue.text.isBlank()) {
                        Text(
                            text = "Task",
                            style = style.copy(color = MaterialTheme.colorScheme.outline)
                        )
                    }
                    innerTextField()
                }
            )
        } else {
            Text(
                text = title.ifBlank { "Task" },
                style = if (title.isBlank()) {
                    style.copy(color = MaterialTheme.colorScheme.outline)
                } else {
                    style
                },
                modifier = Modifier.pointerInput(title, onTap, onDoubleTap) {
                    detectTapGestures(
                        onTap = {
                            onTap()
                            fieldValue = TextFieldValue(
                                text = title,
                                selection = TextRange(title.length)
                            )
                            isEditing = true
                            pendingFocusRequest = true
                        },
                        onDoubleTap = {
                            onDoubleTap()
                        }
                    )
                }
            )
        }

        trailingContent()
    }

    LaunchedEffect(isEditing, pendingFocusRequest) {
        if (isEditing && pendingFocusRequest) {
            withFrameNanos { }
            runCatching {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
            pendingFocusRequest = false
        }
    }
}
