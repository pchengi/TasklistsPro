package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LeadingControls(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(modifier = modifier) {
        TaskDragHandle(
            modifier = Modifier.padding(start = 2.dp, end = 3.dp)
        )

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(36.dp)
        )
    }
}
