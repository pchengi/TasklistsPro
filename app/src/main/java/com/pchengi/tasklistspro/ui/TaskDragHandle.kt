package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

private const val DRAG_STEP_PX = 42f
private const val INDENT_STEP_PX = 56f

@Composable
fun TaskDragHandle(
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onIndent: () -> Unit,
    onOutdent: () -> Unit,
    modifier: Modifier = Modifier
) {
    var verticalOffset by remember { mutableFloatStateOf(0f) }
    var horizontalOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
                if (isDragging) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .pointerInput(onMoveUp, onMoveDown, onIndent, onOutdent) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        verticalOffset = 0f
                        horizontalOffset = 0f
                    },
                    onDragEnd = {
                        isDragging = false
                        verticalOffset = 0f
                        horizontalOffset = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        verticalOffset = 0f
                        horizontalOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        verticalOffset += dragAmount.y
                        horizontalOffset += dragAmount.x

                        when {
                            verticalOffset <= -DRAG_STEP_PX -> {
                                onMoveUp()
                                verticalOffset = 0f
                            }
                            verticalOffset >= DRAG_STEP_PX -> {
                                onMoveDown()
                                verticalOffset = 0f
                            }
                        }

                        when {
                            horizontalOffset >= INDENT_STEP_PX -> {
                                onIndent()
                                horizontalOffset = 0f
                            }
                            horizontalOffset <= -INDENT_STEP_PX -> {
                                onOutdent()
                                horizontalOffset = 0f
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = "Drag handle",
            tint = if (isDragging) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(20.dp)
        )
    }
}
