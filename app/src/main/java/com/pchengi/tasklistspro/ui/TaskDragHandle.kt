package com.pchengi.tasklistspro.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

private const val MOVE_THRESHOLD = 48f
private const val INDENT_THRESHOLD = 64f

@Composable
fun TaskDragHandle(
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onIndent: () -> Unit,
    onOutdent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalX = remember { mutableFloatStateOf(0f) }
    val totalY = remember { mutableFloatStateOf(0f) }

    Icon(
        Icons.Rounded.DragHandle,
        contentDescription = "Drag handle",
        tint = MaterialTheme.colorScheme.outline,
        modifier = modifier
            .padding(horizontal = 4.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        totalX.floatValue = 0f
                        totalY.floatValue = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalX.floatValue += dragAmount.x
                        totalY.floatValue += dragAmount.y
                    },
                    onDragEnd = {
                        val x = totalX.floatValue
                        val y = totalY.floatValue
                        if (kotlin.math.abs(x) > kotlin.math.abs(y) && kotlin.math.abs(x) > INDENT_THRESHOLD) {
                            if (x > 0f) onIndent() else onOutdent()
                        } else if (kotlin.math.abs(y) > MOVE_THRESHOLD) {
                            if (y < 0f) onMoveUp() else onMoveDown()
                        }
                    }
                )
            }
    )
}
