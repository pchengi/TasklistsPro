package com.pchengi.tasklistspro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pchengi.tasklistspro.ui.TaskListScreen
import com.pchengi.tasklistspro.ui.theme.TasklistsProTheme
import com.pchengi.tasklistspro.viewmodel.TaskViewModel

private const val PREFS_NAME = "tasklistspro_settings"
private const val PREF_DARK_MODE = "dark_mode"
private const val PREF_LIGHT_BACKGROUND = "light_background"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDark = isSystemInDarkTheme()
            val prefs = remember { getSharedPreferences(PREFS_NAME, MODE_PRIVATE) }

            var darkMode by remember {
                mutableStateOf(prefs.getBoolean(PREF_DARK_MODE, systemDark))
            }
            var lightBackgroundArgb by remember {
                mutableIntStateOf(
                    prefs.getInt(
                        PREF_LIGHT_BACKGROUND,
                        Color.White.toArgb()
                    )
                )
            }

            val lightBackgroundColor = Color(lightBackgroundArgb)

            TasklistsProTheme(
                darkTheme = darkMode,
                lightBackgroundColor = lightBackgroundColor
            ) {
                val taskViewModel: TaskViewModel = viewModel()
                TaskListScreen(
                    viewModel = taskViewModel,
                    darkMode = darkMode,
                    lightBackgroundColor = lightBackgroundColor,
                    onToggleDarkMode = {
                        darkMode = !darkMode
                        prefs.edit()
                            .putBoolean(PREF_DARK_MODE, darkMode)
                            .apply()
                    },
                    onLightBackgroundSelected = { color ->
                        lightBackgroundArgb = color.toArgb()
                        prefs.edit()
                            .putInt(PREF_LIGHT_BACKGROUND, lightBackgroundArgb)
                            .apply()
                    }
                )
            }
        }
    }
}
