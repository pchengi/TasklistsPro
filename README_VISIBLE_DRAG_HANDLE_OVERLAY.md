# Visible drag handle overlay

This overlay intentionally makes only one small correction:

- every task row shows a visible drag handle at the far right;
- existing inline editing, bold long-press, delete, add-child, expand/collapse, and completion behavior are preserved;
- the handle is visual only in this patch.

Copy these files over the existing repository files, then build.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
