package com.pchengi.tasklistspro.repository

import com.pchengi.tasklistspro.data.TaskDao
import com.pchengi.tasklistspro.data.TaskEntity
import kotlinx.coroutines.flow.Flow

private const val MAX_DEPTH = 3

class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<TaskEntity>> = dao.observeTasks()

    suspend fun addTask(parentId: Long? = null, title: String = ""): Long {
        val all = dao.getAllTasks()
        val newDepth = newDepth(parentId, all)
        require(newDepth <= MAX_DEPTH) { "Maximum subtask depth is 3 levels." }
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
        val changed = mutableListOf(target.copy(completed = completed, updatedAt = System.currentTimeMillis()))
        if (completed) {
            changed += descendants.map { it.copy(completed = true, updatedAt = System.currentTimeMillis()) }
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
        val siblings = siblingsOf(task, all)
        val index = siblings.indexOfFirst { it.id == id }
        if (index <= 0) return
        swapSortOrders(task, siblings[index - 1])
    }

    suspend fun moveDown(id: Long) {
        val all = dao.getAllTasks()
        val task = all.firstOrNull { it.id == id } ?: return
        val siblings = siblingsOf(task, all)
        val index = siblings.indexOfFirst { it.id == id }
        if (index < 0 || index >= siblings.lastIndex) return
        swapSortOrders(task, siblings[index + 1])
    }

    suspend fun indentUnder(id: Long, newParentId: Long?) {
        if (newParentId == null) return
        val all = dao.getAllTasks()
        val task = all.firstOrNull { it.id == id } ?: return
        if (task.id == newParentId) return
        if (descendantsOf(task.id, all).any { it.id == newParentId }) return
        val targetDepth = newDepth(newParentId, all)
        if (targetDepth + subtreeHeightOffset(task.id, all) > MAX_DEPTH) return
        dao.update(
            task.copy(
                parentId = newParentId,
                sortOrder = dao.nextSortOrder(newParentId),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun outdent(id: Long) {
        val all = dao.getAllTasks()
        val task = all.firstOrNull { it.id == id } ?: return
        val parent = all.firstOrNull { it.id == task.parentId } ?: return
        dao.update(
            task.copy(
                parentId = parent.parentId,
                sortOrder = dao.nextSortOrder(parent.parentId),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    private suspend fun swapSortOrders(first: TaskEntity, second: TaskEntity) {
        val now = System.currentTimeMillis()
        dao.updateAll(
            listOf(
                first.copy(sortOrder = second.sortOrder, updatedAt = now),
                second.copy(sortOrder = first.sortOrder, updatedAt = now)
            )
        )
    }

    private suspend fun syncAncestors(startParentId: Long?) {
        var parentId = startParentId
        while (parentId != null) {
            val all = dao.getAllTasks()
            val parent = all.firstOrNull { it.id == parentId } ?: return
            val children = all.filter { it.parentId == parentId }
            val shouldBeComplete = children.isNotEmpty() && children.all { it.completed }
            if (parent.completed != shouldBeComplete) {
                dao.update(parent.copy(completed = shouldBeComplete, updatedAt = System.currentTimeMillis()))
            }
            parentId = parent.parentId
        }
    }

    private fun siblingsOf(task: TaskEntity, all: List<TaskEntity>): List<TaskEntity> =
        all.filter { it.parentId == task.parentId }
            .sortedWith(compareBy<TaskEntity> { it.sortOrder }.thenBy { it.id })

    private fun descendantsOf(id: Long, all: List<TaskEntity>): List<TaskEntity> {
        val children = all.filter { it.parentId == id }
        return children + children.flatMap { descendantsOf(it.id, all) }
    }

    private fun newDepth(parentId: Long?, all: List<TaskEntity>): Int {
        var depth = 0
        var current = parentId
        while (current != null) {
            depth += 1
            current = all.firstOrNull { it.id == current }?.parentId
        }
        return depth
    }

    private fun subtreeHeightOffset(id: Long, all: List<TaskEntity>): Int {
        val children = all.filter { it.parentId == id }
        if (children.isEmpty()) return 0
        return 1 + children.maxOf { subtreeHeightOffset(it.id, all) }
    }
}
