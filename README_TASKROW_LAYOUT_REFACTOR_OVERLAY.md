# TaskRow layout refactor overlay

This overlay refactors the row into smaller composables and fixes the layout model.

Files:
- InlineTaskTitle.kt
- TaskDragHandle.kt
- LeadingControls.kt
- TitleCluster.kt
- TrailingControls.kt
- SwipeDeleteContainer.kt
- TaskRow.kt

Expected behavior:
- Double-tap title toggles bold.
- Tap row or title reveals the hidden + at the far right.
- Expand caret and unchecked-descendant count are part of the title cluster and should sit immediately after the title.
- A flexible spacer appears after the title cluster, not inside it.
- Swipe right deletes leaf tasks.
- Swipe right on parents shows delete confirmation.
- Three-dot drag handle stays at the far left.

Manual checks:
1. Build.
2. Double-tap a task title: bold should toggle.
3. Tap a row/title: + appears at far right.
4. Create subtasks: caret/count should sit directly after task title.
5. Swipe a leaf task right: it deletes.
6. Swipe a parent right: confirmation dialog appears.
