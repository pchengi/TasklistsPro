package com.pchengi.tasklistspro.repository

import android.util.Xml
import com.pchengi.tasklistspro.data.TaskDao
import com.pchengi.tasklistspro.data.TaskEntity
import java.io.StringReader
import java.io.StringWriter
import kotlinx.coroutines.flow.Flow
import org.xmlpull.v1.XmlPullParser

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

    suspend fun exportXml(): String {
        val tasks = dao.getAllTasks()
            .sortedWith(compareBy<TaskEntity> { it.parentId ?: Long.MIN_VALUE }.thenBy { it.sortOrder }.thenBy { it.id })

        val writer = StringWriter()
        val serializer = Xml.newSerializer()
        serializer.setOutput(writer)
        serializer.startDocument("UTF-8", true)
        serializer.startTag(null, "tasklistspro")
        serializer.attribute(null, "version", "1")

        tasks.forEach { task ->
            serializer.startTag(null, "task")
            serializer.attribute(null, "id", task.id.toString())
            task.parentId?.let { serializer.attribute(null, "parentId", it.toString()) }
            serializer.attribute(null, "title", task.title)
            serializer.attribute(null, "completed", task.completed.toString())
            serializer.attribute(null, "bold", task.bold.toString())
            serializer.attribute(null, "expanded", task.expanded.toString())
            serializer.attribute(null, "sortOrder", task.sortOrder.toString())
            serializer.attribute(null, "createdAt", task.createdAt.toString())
            serializer.attribute(null, "updatedAt", task.updatedAt.toString())
            serializer.endTag(null, "task")
        }

        serializer.endTag(null, "tasklistspro")
        serializer.endDocument()
        return writer.toString()
    }

    suspend fun importXml(xml: String) {
        val parser = Xml.newPullParser()
        parser.setInput(StringReader(xml))

        val importedTasks = mutableListOf<TaskEntity>()
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name == "task") {
                importedTasks += parser.readTaskEntity()
            }
            eventType = parser.next()
        }

        val importedIds = importedTasks.map { it.id }.toSet()
        require(importedTasks.isNotEmpty()) { "No tasks found in XML file." }
        require(importedTasks.all { it.id > 0 }) { "Imported tasks must have positive IDs." }
        require(importedTasks.distinctBy { it.id }.size == importedTasks.size) { "Duplicate task IDs in XML file." }
        require(importedTasks.all { task -> task.parentId == null || task.parentId in importedIds }) {
            "XML file contains tasks with missing parents."
        }

        dao.replaceAll(importedTasks)
    }

    private fun XmlPullParser.readTaskEntity(): TaskEntity {
        val id = requiredLong("id")
        return TaskEntity(
            id = id,
            parentId = optionalLong("parentId"),
            title = getAttributeValue(null, "title").orEmpty(),
            completed = optionalBoolean("completed", default = false),
            bold = optionalBoolean("bold", default = false),
            expanded = optionalBoolean("expanded", default = true),
            sortOrder = optionalInt("sortOrder", default = 0),
            createdAt = optionalLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = optionalLong("updatedAt") ?: System.currentTimeMillis()
        )
    }

    private fun XmlPullParser.requiredLong(attributeName: String): Long =
        getAttributeValue(null, attributeName)?.toLongOrNull()
            ?: error("Missing or invalid '$attributeName' attribute.")

    private fun XmlPullParser.optionalLong(attributeName: String): Long? =
        getAttributeValue(null, attributeName)?.takeIf { it.isNotBlank() }?.toLongOrNull()

    private fun XmlPullParser.optionalInt(attributeName: String, default: Int): Int =
        getAttributeValue(null, attributeName)?.toIntOrNull() ?: default

    private fun XmlPullParser.optionalBoolean(attributeName: String, default: Boolean): Boolean =
        getAttributeValue(null, attributeName)?.toBooleanStrictOrNull() ?: default

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
