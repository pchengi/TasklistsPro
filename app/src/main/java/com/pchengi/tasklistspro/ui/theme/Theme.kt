package com.pchengi.tasklistspro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun TasklistsProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    lightBackgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColors
    } else {
        LightColors.copy(
            background = lightBackgroundColor,
            surface = lightBackgroundColor,
            surfaceContainerLowest = lightBackgroundColor,
            surfaceContainerLow = lightBackgroundColor,
            surfaceContainer = lightBackgroundColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
