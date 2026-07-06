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
    onDoubleTap: () -> Unit,
    onFocusHandled: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var fieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = title,
                selection = TextRange(title.length)
            )
        )
    }

    LaunchedEffect(title) {
        if (title != fieldValue.text) {
            fieldValue = TextFieldValue(
                text = title,
                selection = TextRange(title.length)
            )
        }
    }

    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            fieldValue = fieldValue.copy(selection = TextRange(fieldValue.text.length))
            focusRequester.requestFocus()
            keyboardController?.show()
            onFocusHandled()
        }
    }

    Row(
        modifier = modifier.pointerInput(onDoubleTap) {
            detectTapGestures(onDoubleTap = { onDoubleTap() })
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            modifier = Modifier
                .focusRequester(focusRequester)
                .weight(1f, fill = false),
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

        trailingContent()
    }
}
