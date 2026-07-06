package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun TitleCluster(
    title: String,
    style: TextStyle,
    requestFocus: Boolean,
    hasChildren: Boolean,
    expanded: Boolean,
    uncheckedDescendantCount: Int,
    onTitleChange: (String) -> Unit,
    onTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onFocusHandled: () -> Unit,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    InlineTaskTitle(
        title = title,
        style = style,
        requestFocus = requestFocus,
        onTitleChange = onTitleChange,
        onTap = onTap,
        onDoubleTap = onDoubleTap,
        onFocusHandled = onFocusHandled,
        modifier = modifier,
        trailingContent = {
            if (hasChildren) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = onToggleExpanded,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.size(19.dp)
                    )
                }

                if (uncheckedDescendantCount > 0) {
                    Text(
                        text = "($uncheckedDescendantCount)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    )
}
