# Title caret + hidden add + bold fix overlay

Changes:
- Attempts a stronger long-press bold fix by observing pointer events at the initial pass without consuming normal text editing.
- Places the expand caret about four spaces after the task title.
- Places the unchecked descendant count immediately after the caret.
- Moves the + button back to the right edge.
- Hides the + button until the row is tapped.
- Keeps the three-vertical-dots drag handle at the far left.
- Keeps swipe-right delete and parent confirmation dialog.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/InlineTaskTitle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt

Manual checks:
1. Long-press a title: bold should toggle on and off.
2. Tap a row: + should appear at the right edge.
3. Caret should sit close to the title with about four spaces separation.
4. Count should sit immediately after the caret.
5. Swipe-right delete should still work.
