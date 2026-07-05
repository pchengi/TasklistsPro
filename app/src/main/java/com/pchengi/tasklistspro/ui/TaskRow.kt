package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pchengi.tasklistspro.model.TaskNode
import com.pchengi.tasklistspro.viewmodel.TaskViewModel

private const val INDENT_DP = 18

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
    val descendantCount = node.descendantCount()
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = if (task.bold) FontWeight.Bold else FontWeight.Normal
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = (node.depth * INDENT_DP).dp,
                end = 6.dp,
                top = 1.dp,
                bottom = 1.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { checked -> viewModel.toggleCompleted(task.id, checked) },
            modifier = Modifier.size(40.dp)
        )

        if (hasChildren) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(58.dp)
            ) {
                IconButton(
                    onClick = { viewModel.toggleExpanded(task.id) },
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors()
                ) {
                    Icon(
                        imageVector = if (task.expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (task.expanded) "Collapse" else "Expand",
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = "($descendantCount)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            Spacer(modifier = Modifier.width(58.dp))
        }

        InlineTaskTitle(
            title = task.title,
            style = textStyle,
            requestFocus = focusedTaskId == task.id,
            onTitleChange = { viewModel.updateTitle(task.id, it) },
            onLongPress = { viewModel.toggleBold(task.id) },
            onFocusHandled = { viewModel.clearFocusRequest(task.id) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(
                onClick = { viewModel.deleteTask(task.id) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Delete task",
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = { viewModel.addTask(task.id) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add subtask",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        TaskDragHandle(
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

private fun TaskNode.descendantCount(): Int =
    children.size + children.sumOf { it.descendantCount() }
