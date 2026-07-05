package com.pchengi.tasklistspro.repository

import com.pchengi.tasklistspro.data.TaskDao
import com.pchengi.tasklistspro.data.TaskEntity
import kotlinx.coroutines.flow.Flow

private const val MAX_SUBTASK_DEPTH = 3

class TaskRepository(private val dao: TaskDao) {
    val tasks: Flow<List<TaskEntity>> = dao.observeTasks()

    suspend fun addTask(parentId: Long? = null, title: String = ""): Long {
        val all = dao.getAllTasks()
        val depth = depthOf(parentId, all)
        require(depth <= MAX_SUBTASK_DEPTH) { "Maximum subtask depth is 3 levels." }
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
        val descendants = descendantsOf(id, all)
        val target = all.firstOrNull { it.id == id } ?: return
        val changed = mutableListOf<TaskEntity>()
        changed += target.copy(completed = completed, updatedAt = System.currentTimeMillis())
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
}
