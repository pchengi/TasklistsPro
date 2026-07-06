# Display/edit title cluster fix overlay

This overlay fixes the two regressions from the previous refactor.

Core change:
- The task title is displayed as intrinsic Text when not editing.
- Tap switches it into editing mode.
- Double-tap on the displayed title toggles bold.
- Because display mode uses Text, the caret/count cluster sits immediately after the visible title.

Files:
- InlineTaskTitle.kt
- TaskDragHandle.kt
- LeadingControls.kt
- TitleCluster.kt
- TrailingControls.kt
- SwipeDeleteContainer.kt
- TaskRow.kt

Manual checks:
1. Build.
2. Double-tap a non-editing title: bold should toggle.
3. Tap a title: it should become editable and reveal the +.
4. Create children: caret/count should sit just after the visible task title.
5. Swipe delete should still work.
