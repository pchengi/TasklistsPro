# TaskRow-owned delete confirmation overlay

This overlay moves delete confirmation state into TaskRow.

Why:
- SwipeDeleteContainer-level confirmation was not displaying reliably.
- TaskRow knows whether the task has children and can own dialog state directly.

Changes:
- SwipeDeleteContainer only reports `onDeleteRequested`.
- TaskRow decides:
  - leaf task: delete immediately
  - parent task: show confirmation dialog
- Double-tap bold code is preserved.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/SwipeDeleteContainer.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
