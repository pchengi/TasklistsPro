package com.pchengi.tasklistspro.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.pchengi.tasklistspro.model.flattenVisible
import com.pchengi.tasklistspro.viewmodel.TaskViewModel
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private const val FILE_PROVIDER_AUTHORITY = "com.pchengi.tasklistspro.fileprovider"

private val PostItYellow = Color(0xFFFFFF88)

private val LightBackgroundOptions = listOf(
    "Post-it Yellow" to PostItYellow,
    "White" to Color(0xFFFFFFFF),
    "Warm cream" to Color(0xFFFFF8E1),
    "Soft yellow" to Color(0xFFFFFDE7),
    "Pale green" to Color(0xFFE8F5E9),
    "Pale blue" to Color(0xFFE3F2FD),
    "Pale lavender" to Color(0xFFF3E5F5),
    "Soft pink" to Color(0xFFFCE4EC),
    "Light grey" to Color(0xFFF5F5F5)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    darkMode: Boolean,
    lightBackgroundColor: Color,
    onToggleDarkMode: () -> Unit,
    onLightBackgroundSelected: (Color) -> Unit
) {
    val context = LocalContext.current
    val tree by viewModel.taskTree.collectAsState()
    val focusedTaskId by viewModel.focusTaskId.collectAsState()
    val visibleTasks = tree.flattenVisible()
    var showDeleteAllConfirmation by remember { mutableStateOf(false) }
    var showBackgroundPicker by remember { mutableStateOf(false) }
    var showCustomColorPicker by remember { mutableStateOf(false) }

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Tasklists Pro") },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (darkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = if (darkMode) "Switch to bright mode" else "Switch to dark mode"
                        )
                    }
                    if (!darkMode) {
                        IconButton(onClick = { showBackgroundPicker = true }) {
                            Icon(Icons.Rounded.Palette, contentDescription = "Choose background color")
                        }
                    }
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

    if (showBackgroundPicker) {
        BackgroundPickerDialog(
            selectedColor = lightBackgroundColor,
            onSelectColor = { color ->
                onLightBackgroundSelected(color)
                showBackgroundPicker = false
            },
            onAddCustomColor = {
                showBackgroundPicker = false
                showCustomColorPicker = true
            },
            onDismiss = { showBackgroundPicker = false }
        )
    }

    if (showCustomColorPicker) {
        CustomColorDialog(
            initialColor = lightBackgroundColor,
            onApply = { color ->
                onLightBackgroundSelected(color)
                showCustomColorPicker = false
            },
            onDismiss = { showCustomColorPicker = false }
        )
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

@Composable
private fun BackgroundPickerDialog(
    selectedColor: Color,
    onSelectColor: (Color) -> Unit,
    onAddCustomColor: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose bright mode background") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LightBackgroundOptions.forEach { (label, color) ->
                    TextButton(onClick = { onSelectColor(color) }) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(color)
                            )
                            Text(
                                text = if (color == selectedColor) "$label ✓" else label
                            )
                        }
                    }
                }

                TextButton(onClick = onAddCustomColor) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Text("Add custom color")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun CustomColorDialog(
    initialColor: Color,
    onApply: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var red by remember(initialColor) {
        mutableIntStateOf((initialColor.red * 255).roundToInt().coerceIn(0, 255))
    }
    var green by remember(initialColor) {
        mutableIntStateOf((initialColor.green * 255).roundToInt().coerceIn(0, 255))
    }
    var blue by remember(initialColor) {
        mutableIntStateOf((initialColor.blue * 255).roundToInt().coerceIn(0, 255))
    }

    val previewColor = Color(
        red = red / 255f,
        green = green / 255f,
        blue = blue / 255f,
        alpha = 1f
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add custom color") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(previewColor)
                        .align(Alignment.CenterHorizontally)
                )

                ColorSliderRow(
                    label = "R",
                    value = red,
                    onValueChange = { red = it }
                )
                ColorSliderRow(
                    label = "G",
                    value = green,
                    onValueChange = { green = it }
                )
                ColorSliderRow(
                    label = "B",
                    value = blue,
                    onValueChange = { blue = it }
                )

                Text("RGB($red, $green, $blue)")
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(previewColor) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ColorSliderRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Text("$label: $value")
        Slider(
            value = value.toFloat(),
            onValueChange = { sliderValue ->
                onValueChange(sliderValue.roundToInt().coerceIn(0, 255))
            },
            valueRange = 0f..255f,
            steps = 254
        )
    }
}

private fun defaultBackupFileName(): String {
    val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    return "TaskListPro_$date.xml"
}
