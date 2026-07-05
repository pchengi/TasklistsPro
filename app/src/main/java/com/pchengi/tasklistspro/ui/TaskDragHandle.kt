package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun TaskDragHandle(
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onIndent: () -> Unit,
    onOutdent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val thresholdPx = with(LocalDensity.current) { 42.dp.toPx() }
    var dragTotal by remember { mutableStateOf(Offset.Zero) }
    var horizontalMoveConsumed by remember { mutableStateOf(false) }

    Icon(
        imageVector = Icons.Rounded.DragHandle,
        contentDescription = "Drag handle",
        tint = MaterialTheme.colorScheme.outline,
        modifier = modifier
            .size(32.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragTotal = Offset.Zero
                        horizontalMoveConsumed = false
                    },
                    onDragCancel = {
                        dragTotal = Offset.Zero
                        horizontalMoveConsumed = false
                    },
                    onDragEnd = {
                        dragTotal = Offset.Zero
                        horizontalMoveConsumed = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragTotal += dragAmount

                        val horizontalDominates = abs(dragTotal.x) > abs(dragTotal.y)
                        if (!horizontalMoveConsumed && horizontalDominates && abs(dragTotal.x) >= thresholdPx) {
                            if (dragTotal.x > 0) {
                                onIndent()
                            } else {
                                onOutdent()
                            }
                            horizontalMoveConsumed = true
                            dragTotal = Offset.Zero
                            return@detectDragGestures
                        }

                        val verticalDominates = abs(dragTotal.y) >= abs(dragTotal.x)
                        if (verticalDominates && abs(dragTotal.y) >= thresholdPx) {
                            if (dragTotal.y < 0) {
                                onMoveUp()
                            } else {
                                onMoveDown()
                            }
                            dragTotal = Offset.Zero
                        }
                    }
                )
            }
    )
}
