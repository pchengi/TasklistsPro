# Tap reveal plus + tight trailing overlay

Fixes:
- The inline title now reports a normal tap, so tapping the title should reveal the hidden +.
- The inline title is no longer weighted, so the caret/count should sit immediately after the text.
- A spacer after the title/caret/count consumes remaining row width, keeping + at the right edge.
- Double-tap bold behavior is preserved.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/InlineTaskTitle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
