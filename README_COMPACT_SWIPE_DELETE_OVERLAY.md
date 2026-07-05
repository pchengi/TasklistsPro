# Compact swipe-delete overlay

This overlay updates the task row UI.

Changes:
- Removes the always-visible delete button.
- Adds swipe-right-to-delete.
- Shows a confirmation dialog when swiping a parent task that has subtasks.
- Keeps leaf-task deletion fast.
- Shows unchecked descendant count after the task title, e.g. `Kitchen (3)`.
- Hides the count when all descendants are checked.
- Further compacts the row:
  - 16dp indentation
  - smaller checkbox footprint
  - smaller expand/collapse footprint
  - smaller add-child button
  - 28dp drag handle
- Keeps the drag handle visible at the far right.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt

Manual checks:
1. Build the app.
2. Create a parent with several subtasks.
3. Confirm the parent title shows only unchecked descendant count.
4. Check all descendants and confirm the count disappears.
5. Swipe a leaf task right and confirm it is deleted.
6. Swipe a parent task right and confirm a confirmation dialog appears.
7. Confirm add-child, inline editing, checkbox sync, long-press bold, and drag handle visibility still work.
