# Double-tap trailing-controls overlay

This overlay fixes the previous attempt by moving caret/count into the title editor as trailing content.

Expected behavior:
- Double-tap on the title area toggles bold.
- Expand caret and unchecked count are rendered immediately after the text inside the same inline title row.
- + remains hidden at the right edge until row tap.
- Three-dot drag handle remains at the left.
- Swipe-right delete remains.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/InlineTaskTitle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskDragHandle.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
