# Reordered row controls overlay

This overlay updates the row layout as requested.

New row order:
- drag handle at the far left
- checkbox next
- `+` add-subtask button where the expand caret used to be
- title
- expand/collapse caret immediately after the title
- unchecked descendant count immediately after the caret
- clean right edge

It preserves:
- swipe right to delete
- confirmation dialog for deleting parents
- inline editing
- long-press title toggles bold
- checkbox completion sync
- compact spacing
- visible drag handle

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt

Manual checks:
1. Build.
2. Confirm drag handle appears at the far left.
3. Confirm `+` appears before the title.
4. Confirm expand caret appears after the title only for rows with children.
5. Confirm unchecked descendant count appears immediately after the caret.
6. Confirm right side is visually clean.
7. Confirm swipe-right delete still works.
