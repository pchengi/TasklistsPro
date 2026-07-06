# Double-tap bold + tight caret overlay

Changes:
- Replaces long-press bold with double-tap bold on the task title.
- Removes long-press handling from the title field, avoiding Android's Paste/Select All popup.
- Moves the caret/count directly after the task title, with no artificial spacer.
- Keeps + hidden at the right edge until the row is tapped.
- Keeps swipe-right delete and parent-delete confirmation.
- Keeps three-dot drag handle at the far left.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/InlineTaskTitle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt

Manual checks:
1. Double-tap title toggles bold on and off.
2. Long-press title should no longer be used for bold.
3. Long-press may still show Android text tools, but it is no longer part of app behavior.
4. Caret and unchecked count should appear immediately after the item text.
5. Tap row to reveal + at the right edge.
