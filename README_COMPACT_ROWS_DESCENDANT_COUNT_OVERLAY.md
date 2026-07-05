# Compact rows + descendant count overlay

This overlay makes a small visual-polish pass.

Changes:
- Reduces vertical row padding.
- Reduces hierarchy indentation to 18dp per level.
- Keeps the visible drag handle at the far right.
- Shrinks delete/add controls slightly.
- Shows total descendant count next to the expand/collapse button, e.g. `▼ (5)`.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt

Manual checks:
1. Build the app.
2. Create a task with several nested subtasks.
3. Confirm the parent row shows a count such as `(3)` beside the expand/collapse button.
4. Confirm rows are more compact than before.
5. Confirm long-press bold, inline editing, delete, add-child, and checkbox behavior still work.
