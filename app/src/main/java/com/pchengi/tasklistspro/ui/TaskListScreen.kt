package com.pchengi.tasklistspro.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.pchengi.tasklistspro.model.flattenVisible
import com.pchengi.tasklistspro.viewmodel.TaskViewModel
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val FILE_PROVIDER_AUTHORITY = "com.pchengi.tasklistspro.fileprovider"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val context = LocalContext.current
    val tree by viewModel.taskTree.collectAsState()
    val focusedTaskId by viewModel.focusTaskId.collectAsState()
    val visibleTasks = tree.flattenVisible()
    var showDeleteAllConfirmation by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/xml")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        viewModel.exportXml(
            onSuccess = { xml ->
                runCatching {
                    context.contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.write(xml.toByteArray(Charsets.UTF_8))
                    } ?: error("Could not open export destination.")
                }.onSuccess {
                    Toast.makeText(context, "Saved XML backup", Toast.LENGTH_SHORT).show()
                }.onFailure { error ->
                    Toast.makeText(
                        context,
                        "Save failed: ${error.message ?: "unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            onError = { error ->
                Toast.makeText(
                    context,
                    "Save failed: ${error.message ?: "unknown error"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        runCatching {
            context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8).use { reader ->
                reader?.readText() ?: error("Could not open import source.")
            }
        }.onSuccess { xml ->
            viewModel.importXml(
                xml = xml,
                onSuccess = {
                    Toast.makeText(context, "Restored XML backup", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(
                        context,
                        "Restore failed: ${error.message ?: "unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }.onFailure { error ->
            Toast.makeText(
                context,
                "Restore failed: ${error.message ?: "unknown error"}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasklists Pro") },
                actions = {
                    IconButton(onClick = { importLauncher.launch(arrayOf("text/xml", "application/xml", "*/*")) }) {
                        Icon(Icons.Rounded.Restore, contentDescription = "Restore XML")
                    }
                    IconButton(onClick = { exportLauncher.launch(defaultBackupFileName()) }) {
                        Icon(Icons.Rounded.Save, contentDescription = "Save XML")
                    }
                    IconButton(
                        onClick = {
                            viewModel.exportXml(
                                onSuccess = { xml ->
                                    runCatching {
                                        val shareDir = File(context.cacheDir, "shared_backups")
                                        shareDir.mkdirs()
                                        val shareFile = File(shareDir, defaultBackupFileName())
                                        shareFile.writeText(xml, Charsets.UTF_8)

                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            FILE_PROVIDER_AUTHORITY,
                                            shareFile
                                        )

                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/xml"
                                            putExtra(Intent.EXTRA_SUBJECT, "TasklistsPro Backup")
                                            putExtra(Intent.EXTRA_TEXT, "Attached is my TasklistsPro backup.")
                                            putExtra(Intent.EXTRA_STREAM, uri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }

                                        context.startActivity(
                                            Intent.createChooser(sendIntent, "Share TasklistsPro backup")
                                        )
                                    }.onFailure { error ->
                                        Toast.makeText(
                                            context,
                                            "Share failed: ${error.message ?: "unknown error"}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                onError = { error ->
                                    Toast.makeText(
                                        context,
                                        "Share failed: ${error.message ?: "unknown error"}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = "Share XML")
                    }
                }
            )
        },
        floatingActionButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(onClick = { showDeleteAllConfirmation = true }) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete entire task list")
                }
                FloatingActionButton(onClick = { viewModel.addTask() }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add top-level task")
                }
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
                itemsIndexed(visibleTasks, key = { _, node -> node.task.id }) { index, node ->
                    TaskRow(
                        node = node,
                        previousVisibleNode = visibleTasks.getOrNull(index - 1),
                        focusedTaskId = focusedTaskId,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (showDeleteAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteAllConfirmation = false },
            title = { Text("Delete entire task list?") },
            text = {
                Text(
                    "This will permanently delete all tasks and subtasks. " +
                        "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAllConfirmation = false
                        viewModel.deleteAll(
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Deleted entire task list",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onError = { error ->
                                Toast.makeText(
                                    context,
                                    "Delete failed: ${error.message ?: "unknown error"}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                ) {
                    Text("Delete everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun defaultBackupFileName(): String {
    val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    return "TaskListPro_$date.xml"
}
