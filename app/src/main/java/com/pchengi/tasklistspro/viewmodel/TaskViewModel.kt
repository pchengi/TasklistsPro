package com.pchengi.tasklistspro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pchengi.tasklistspro.data.TaskDatabase
import com.pchengi.tasklistspro.data.TaskEntity
import com.pchengi.tasklistspro.model.TaskNode
import com.pchengi.tasklistspro.model.toTaskTree
import com.pchengi.tasklistspro.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(TaskDatabase.get(application).taskDao())

    val taskTree: StateFlow<List<TaskNode>> = repository.tasks
        .map(List<TaskEntity>::toTaskTree)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _focusTaskId = MutableStateFlow<Long?>(null)
    val focusTaskId: StateFlow<Long?> = _focusTaskId

    fun addTask(parentId: Long? = null) {
        viewModelScope.launch {
            val id = repository.addTask(parentId)
            _focusTaskId.value = id
        }
    }

    fun clearFocusRequest(id: Long) {
        if (_focusTaskId.value == id) _focusTaskId.value = null
    }

    fun updateTitle(id: Long, title: String) {
        viewModelScope.launch { repository.updateTitle(id, title) }
    }

    fun toggleCompleted(id: Long, completed: Boolean) {
        viewModelScope.launch { repository.setCompleted(id, completed) }
    }

    fun toggleBold(id: Long) {
        viewModelScope.launch { repository.toggleBold(id) }
    }

    fun toggleExpanded(id: Long) {
        viewModelScope.launch { repository.toggleExpanded(id) }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch { repository.deleteTask(id) }
    }

    fun moveUp(id: Long) {
        viewModelScope.launch { repository.moveUp(id) }
    }

    fun moveDown(id: Long) {
        viewModelScope.launch { repository.moveDown(id) }
    }

    fun indentUnder(id: Long, parentId: Long?) {
        viewModelScope.launch { repository.indentUnder(id, parentId) }
    }

    fun outdent(id: Long) {
        viewModelScope.launch { repository.outdent(id) }
    }
}
