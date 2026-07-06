package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
    val uncheckedDescendantCount = node.uncheckedDescendantCount()
    var showAddButton by remember(task.id) { mutableStateOf(false) }

    val revealAdd = { showAddButton = true }

    SwipeDeleteContainer(
        title = task.title,
        descendantCount = descendantCount,
        onDelete = { viewModel.deleteTask(task.id) },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = revealAdd)
                .padding(
                    start = (node.depth * INDENT_DP).dp,
                    end = 6.dp,
                    top = 1.dp,
                    bottom = 1.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LeadingControls(
                checked = task.completed,
                onCheckedChange = { checked -> viewModel.toggleCompleted(task.id, checked) }
            )

            TitleCluster(
                title = task.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (task.bold) FontWeight.Bold else FontWeight.Normal
                ),
                requestFocus = focusedTaskId == task.id,
                hasChildren = hasChildren,
                expanded = task.expanded,
                uncheckedDescendantCount = uncheckedDescendantCount,
                onTitleChange = { viewModel.updateTitle(task.id, it) },
                onTap = revealAdd,
                onDoubleTap = { viewModel.toggleBold(task.id) },
                onFocusHandled = { viewModel.clearFocusRequest(task.id) },
                onToggleExpanded = { viewModel.toggleExpanded(task.id) },
                modifier = Modifier.padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            TrailingControls(
                showAddButton = showAddButton,
                onAddSubtask = { viewModel.addTask(task.id) }
            )
        }
    }
}

private fun TaskNode.descendantCount(): Int =
    children.size + children.sumOf { it.descendantCount() }

private fun TaskNode.uncheckedDescendantCount(): Int =
    children.count { !it.task.completed } + children.sumOf { it.uncheckedDescendantCount() }
