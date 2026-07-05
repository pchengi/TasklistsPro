package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

@Composable
fun InlineTaskTitle(
    title: String,
    style: TextStyle,
    onTitleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = title,
        onValueChange = onTitleChange,
        singleLine = true,
        textStyle = style,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier
    )
}
