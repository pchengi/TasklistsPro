# Complete drag nesting overlay

This overlay makes the drag-handle API and all call sites consistent.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/LeadingControls.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
- app/src/main/java/com/pchengi/tasklistspro/viewmodel/TaskViewModel.kt
- app/src/main/java/com/pchengi/tasklistspro/repository/TaskRepository.kt

Behavior:
- Drag handle up/down: reorder among siblings.
- Drag handle right: indent under previous visible task.
- Drag handle left: outdent.
- Nesting depth remains limited to 3 levels.
- Existing delete confirmation, double-tap bold, and tap-to-show-plus behavior are preserved.

Notes:
- Apply these files together. The compile error about missing `onIndent` / `onOutdent` happens when only some of the UI files are updated.
