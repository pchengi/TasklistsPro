package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pchengi.tasklistspro.model.TaskNode
import com.pchengi.tasklistspro.viewmodel.TaskViewModel

@Composable
fun TaskRow(
    node: TaskNode,
    previousVisibleNode: TaskNode?,
    focusedTaskId: Long?,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val task = node.task
    val hasChildren = node.children.isNotEmpty()
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = if (task.bold) FontWeight.Bold else FontWeight.Normal
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = (node.depth * 28).dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskDragHandle(
            onMoveUp = { viewModel.moveUp(task.id) },
            onMoveDown = { viewModel.moveDown(task.id) },
            onIndent = { viewModel.indentUnder(task.id, previousVisibleNode?.task?.id) },
            onOutdent = { viewModel.outdent(task.id) }
        )

        Checkbox(
            checked = task.completed,
            onCheckedChange = { checked -> viewModel.toggleCompleted(task.id, checked) }
        )

        if (hasChildren) {
            IconButton(onClick = { viewModel.toggleExpanded(task.id) }) {
                Icon(
                    imageVector = if (task.expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (task.expanded) "Collapse" else "Expand"
                )
            }
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        InlineTaskTitle(
            title = task.title,
            style = textStyle,
            requestFocus = focusedTaskId == task.id,
            onTitleChange = { viewModel.updateTitle(task.id, it) },
            onLongPress = { viewModel.toggleBold(task.id) },
            onFocusHandled = { viewModel.clearFocusRequest(task.id) },
            modifier = Modifier.weight(1f)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = { viewModel.deleteTask(task.id) }) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete task")
            }
            IconButton(onClick = { viewModel.addTask(task.id) }) {
                Icon(Icons.Rounded.Add, contentDescription = "Add subtask")
            }
        }
    }
}
