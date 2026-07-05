package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pchengi.tasklistspro.model.TaskNode
import com.pchengi.tasklistspro.model.flattenVisible
import com.pchengi.tasklistspro.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val tree by viewModel.taskTree.collectAsState()
    val visibleTasks = tree.flattenVisible()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tasklists Pro") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addTask() }) {
                Icon(Icons.Rounded.Add, contentDescription = "Add top-level task")
            }
        }
    ) { innerPadding ->
        if (visibleTasks.isEmpty()) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(visibleTasks, key = { it.task.id }) { node ->
                    TaskRow(node = node, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tasks yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tap + to create your first task.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskRow(node: TaskNode, viewModel: TaskViewModel) {
    val task = node.task
    val hasChildren = node.children.isNotEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (node.depth * 28).dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.DragHandle,
            contentDescription = "Drag handle",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 4.dp)
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

        OutlinedTextField(
            value = task.title,
            onValueChange = { viewModel.updateTitle(task.id, it) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (task.bold) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier
                .weight(1f)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { viewModel.toggleBold(task.id) }
                ),
            placeholder = { Text("Task") }
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
