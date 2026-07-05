# Row layout + bold fix overlay

Changes:
- Restores long-press title -> bold toggle by applying long-press detection directly to the inline text field.
- Moves the expand/collapse caret immediately after the task title.
- Places the unchecked descendant count immediately after the caret.
- Moves the `+` button to the right of the count with a small gap.
- Switches the drag handle icon to three vertical dots.
- Keeps the drag handle at the left-most position before the checkbox.
- Keeps swipe-right delete and parent-delete confirmation.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/InlineTaskTitle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt

Manual checks:
1. Long-press the title: bold should toggle on/off.
2. Create subtasks: caret should appear immediately after the title.
3. The unchecked-descendant count should be immediately after the caret.
4. The + button should be after the count with a small separation.
5. The drag handle should appear as three vertical dots at the far left.
6. Swipe-right delete should still work.
