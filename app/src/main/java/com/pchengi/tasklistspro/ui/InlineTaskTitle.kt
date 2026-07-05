package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InlineTaskTitle(
    title: String,
    style: TextStyle,
    requestFocus: Boolean,
    onTitleChange: (String) -> Unit,
    onLongPress: () -> Unit,
    onFocusHandled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
            keyboardController?.show()
            onFocusHandled()
        }
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = title,
            onValueChange = onTitleChange,
            singleLine = true,
            textStyle = style,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .focusRequester(focusRequester)
                .combinedClickable(
                    onClick = {},
                    onLongClick = onLongPress
                ),
            decorationBox = { innerTextField ->
                if (title.isBlank()) {
                    Text(
                        text = "Task",
                        style = style.copy(color = MaterialTheme.colorScheme.outline)
                    )
                }
                innerTextField()
            }
        )
    }
}
