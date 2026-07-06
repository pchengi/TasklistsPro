package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrailingControls(
    showAddButton: Boolean,
    onAddSubtask: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showAddButton) {
        FilledTonalIconButton(
            onClick = onAddSubtask,
            modifier = modifier.size(30.dp)
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Add subtask",
                modifier = Modifier.size(18.dp)
            )
        }
    } else {
        Spacer(modifier = modifier.size(30.dp))
    }
}
