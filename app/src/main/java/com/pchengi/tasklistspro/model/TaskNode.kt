package com.pchengi.tasklistspro.model

import com.pchengi.tasklistspro.data.TaskEntity

data class TaskNode(
    val task: TaskEntity,
    val depth: Int,
    val children: List<TaskNode>
)

fun List<TaskEntity>.toTaskTree(): List<TaskNode> {
    val byParent = groupBy { it.parentId }

    fun build(parentId: Long?, depth: Int): List<TaskNode> =
        byParent[parentId]
            .orEmpty()
            .sortedWith(compareBy<TaskEntity> { it.sortOrder }.thenBy { it.id })
            .map { task ->
                TaskNode(
                    task = task,
                    depth = depth,
                    children = build(task.id, depth + 1)
                )
            }

    return build(null, 0)
}

fun List<TaskNode>.flattenVisible(): List<TaskNode> = flatMap { node ->
    if (node.task.expanded) listOf(node) + node.children.flattenVisible() else listOf(node)
}
