package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pchengi.tasklistspro.model.TaskNode
import com.pchengi.tasklistspro.viewmodel.TaskViewModel

private const val INDENT_DP = 16

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskRow(
    node: TaskNode,
    previousVisibleNode: TaskNode?,
    focusedTaskId: Long?,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val task = node.task
    val descendantCount = node.descendantCount()
    val uncheckedDescendantCount = node.uncheckedDescendantCount()
    var confirmDelete by remember(task.id) { mutableStateOf(false) }
    var showAddButton by remember(task.id) { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {
                if (descendantCount > 0) {
                    confirmDelete = true
                    false
                } else {
                    viewModel.deleteTask(task.id)
                    true
                }
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(start = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        modifier = modifier
    ) {
        TaskRowContents(
            node = node,
            focusedTaskId = focusedTaskId,
            uncheckedDescendantCount = uncheckedDescendantCount,
            showAddButton = showAddButton,
            onRevealAdd = { showAddButton = true },
            viewModel = viewModel
        )
    }

    if (confirmDelete) {
        DeleteTaskConfirmationDialog(
            title = task.title.ifBlank { "Untitled task" },
            descendantCount = descendantCount,
            onDismiss = { confirmDelete = false },
            onConfirm = {
                confirmDelete = false
                viewModel.deleteTask(task.id)
            }
        )
    }
}

@Composable
private fun TaskRowContents(
    node: TaskNode,
    focusedTaskId: Long?,
    uncheckedDescendantCount: Int,
    showAddButton: Boolean,
    onRevealAdd: () -> Unit,
    viewModel: TaskViewModel
) {
    val task = node.task
    val hasChildren = node.children.isNotEmpty()
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = if (task.bold) FontWeight.Bold else FontWeight.Normal
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onRevealAdd)
            .padding(
                start = (node.depth * INDENT_DP).dp,
                end = 6.dp,
                top = 1.dp,
                bottom = 1.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskDragHandle(
            modifier = Modifier.padding(start = 2.dp, end = 3.dp)
        )

        Checkbox(
            checked = task.completed,
            onCheckedChange = { checked -> viewModel.toggleCompleted(task.id, checked) },
            modifier = Modifier.size(36.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InlineTaskTitle(
                title = task.title,
                style = textStyle,
                requestFocus = focusedTaskId == task.id,
                onTitleChange = { viewModel.updateTitle(task.id, it) },
                onLongPress = { viewModel.toggleBold(task.id) },
                onFocusHandled = { viewModel.clearFocusRequest(task.id) },
                modifier = Modifier.weight(1f, fill = false)
            )

            if (hasChildren) {
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { viewModel.toggleExpanded(task.id) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = if (task.expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (task.expanded) "Collapse" else "Expand",
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (uncheckedDescendantCount > 0) {
                    Text(
                        text = "($uncheckedDescendantCount)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 1.dp)
                    )
                }
            }
        }

        if (showAddButton) {
            FilledTonalIconButton(
                onClick = { viewModel.addTask(task.id) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add subtask",
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
private fun DeleteTaskConfirmationDialog(
    title: String,
    descendantCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete task?") },
        text = {
            Text(
                "Delete \"$title\"? This will also delete $descendantCount subtask" +
                    if (descendantCount == 1) "." else "s."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun TaskNode.descendantCount(): Int =
    children.size + children.sumOf { it.descendantCount() }

private fun TaskNode.uncheckedDescendantCount(): Int =
    children.count { !it.task.completed } + children.sumOf { it.uncheckedDescendantCount() }
