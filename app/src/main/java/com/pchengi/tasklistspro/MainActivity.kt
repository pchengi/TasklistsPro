package com.pchengi.tasklistspro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pchengi.tasklistspro.ui.TaskListScreen
import com.pchengi.tasklistspro.ui.theme.TasklistsProTheme
import com.pchengi.tasklistspro.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasklistsProTheme {
                val taskViewModel: TaskViewModel = viewModel()
                TaskListScreen(viewModel = taskViewModel)
            }
        }
    }
}
