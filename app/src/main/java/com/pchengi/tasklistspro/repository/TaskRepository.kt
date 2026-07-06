package com.pchengi.tasklistspro.repository

import com.pchengi.tasklistspro.data.TaskDao
import com.pchengi.tasklistspro.data.TaskEntity
import kotlinx.coroutines.flow.Flow

private const val MAX_SUBTASK_DEPTH = 3

class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<TaskEntity>> = dao.observeTasks()

    suspend fun addTask(parentId: Long? = null, title: String = ""): Long {
        val all = dao.getAllTasks()
        val parentDepth = depthOf(parentId, all)
        require(parentDepth <= MAX_SUBTASK_DEPTH) { "Maximum subtask depth is 3 levels." }
        return dao.insert(
            TaskEntity(
                parentId = parentId,
                title = title,
                sortOrder = dao.nextSortOrder(parentId)
            )
        )
    }

    suspend fun updateTitle(id: Long, title: String) {
        val task = dao.getAllTasks().firstOrNull { it.id == id } ?: return
        dao.update(task.copy(title = title, updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleBold(id: Long) {
        val task = dao.getAllTasks().firstOrNull { it.id == id } ?: return
        dao.update(task.copy(bold = !task.bold, updatedAt = System.currentTimeMillis()))
    }

    suspend fun toggleExpanded(id: Long) {
        val task = dao.getAllTasks().firstOrNull { it.id == id } ?: return
        dao.update(task.copy(expanded = !task.expanded, updatedAt = System.currentTimeMillis()))
    }

    suspend fun setCompleted(id: Long, completed: Boolean) {
        val all = dao.getAllTasks()
        val target = all.firstOrNull { it.id == id } ?: return
        val descendants = descendantsOf(id, all)
        val now = System.currentTimeMillis()

        val changed = mutableListOf<TaskEntity>()
        changed += target.copy(completed = completed, updatedAt = now)
        if (completed) {
            changed += descendants.map { it.copy(completed = true, updatedAt = now) }
        }

        dao.updateAll(changed)
        syncAncestors(target.parentId)
    }

    suspend fun deleteTask(id: Long) {
        val all = dao.getAllTasks()
        val ids = descendantsOf(id, all).map { it.id } + id
        dao.deleteIds(ids)
    }

    suspend fun moveUp(id: Long) {
        val all = dao.getAllTasks()
        val task = all.firstOrNull { it.id == id } ?: return
        val siblings = siblingsOf(task.parentId, all)
        val index = siblings.indexOfFirst { it.id == id }
        if (index <= 0) return

        val previous = siblings[index - 1]
        val now = System.currentTimeMillis()
        dao.updateAll(
            listOf(
                task.copy(sortOrder = previous.sortOrder, updatedAt = now),
                previous.copy(sortOrder = task.sortOrder, updatedAt = now)
            )
        )
    }

    suspend fun moveDown(id: Long) {
        val all = dao.getAllTasks()
        val task = all.firstOrNull { it.id == id } ?: return
        val siblings = siblingsOf(task.parentId, all)
        val index = siblings.indexOfFirst { it.id == id }
        if (index < 0 || index >= siblings.lastIndex) return

        val next = siblings[index + 1]
        val now = System.currentTimeMillis()
        dao.updateAll(
            listOf(
                task.copy(sortOrder = next.sortOrder, updatedAt = now),
                next.copy(sortOrder = task.sortOrder, updatedAt = now)
            )
        )
    }

    suspend fun indentUnder(id: Long, newParentId: Long?) {
        if (newParentId == null) return

        val all = dao.getAllTasks()
        val task = all.firstOrNull { it.id == id } ?: return
        if (task.id == newParentId) return
        if (descendantsOf(task.id, all).any { it.id == newParentId }) return

        val oldParentId = task.parentId
        val parentDepth = depthOf(newParentId, all)
        val movedSubtreeHeight = subtreeHeight(task.id, all)
        if (parentDepth + movedSubtreeHeight > MAX_SUBTASK_DEPTH) return

        val now = System.currentTimeMillis()
        dao.update(
            task.copy(
                parentId = newParentId,
                sortOrder = dao.nextSortOrder(newParentId),
                updatedAt = now
            )
        )

        syncAncestors(oldParentId)
        syncAncestors(newParentId)
    }

    suspend fun outdent(id: Long) {
        val all = dao.getAllTasks()
        val task = all.firstOrNull { it.id == id } ?: return
        val parent = all.firstOrNull { it.id == task.parentId } ?: return

        val oldParentId = task.parentId
        val now = System.currentTimeMillis()
        dao.update(
            task.copy(
                parentId = parent.parentId,
                sortOrder = dao.nextSortOrder(parent.parentId),
                updatedAt = now
            )
        )

        syncAncestors(oldParentId)
        syncAncestors(parent.parentId)
    }

    private suspend fun syncAncestors(startParentId: Long?) {
        var parentId = startParentId
        while (parentId != null) {
            val all = dao.getAllTasks()
            val parent = all.firstOrNull { it.id == parentId } ?: return
            val children = all.filter { it.parentId == parentId }
            val shouldBeComplete = children.isNotEmpty() && children.all { it.completed }

            if (parent.completed != shouldBeComplete) {
                dao.update(
                    parent.copy(
                        completed = shouldBeComplete,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
            parentId = parent.parentId
        }
    }

    private fun siblingsOf(parentId: Long?, all: List<TaskEntity>): List<TaskEntity> =
        all.filter { it.parentId == parentId }
            .sortedWith(compareBy<TaskEntity> { it.sortOrder }.thenBy { it.id })

    private fun descendantsOf(id: Long, all: List<TaskEntity>): List<TaskEntity> {
        val children = all.filter { it.parentId == id }
        return children + children.flatMap { descendantsOf(it.id, all) }
    }

    private fun depthOf(parentId: Long?, all: List<TaskEntity>): Int {
        var depth = 0
        var current = parentId
        while (current != null) {
            depth += 1
            current = all.firstOrNull { it.id == current }?.parentId
        }
        return depth
    }

    private fun subtreeHeight(id: Long, all: List<TaskEntity>): Int {
        val children = all.filter { it.parentId == id }
        if (children.isEmpty()) return 1
        return 1 + children.maxOf { subtreeHeight(it.id, all) }
    }
}
