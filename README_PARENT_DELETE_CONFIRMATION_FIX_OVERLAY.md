# Parent delete confirmation fix overlay

GitHub connector blocked the direct push for this small fix, so this overlay contains only two files.

Changes:
- `SwipeDeleteContainer` now takes an explicit `requireConfirmation` flag.
- `TaskRow` passes `requireConfirmation = node.children.isNotEmpty()`.
- This decouples the confirmation trigger from the descendant count rendering path.

Files:
- app/src/main/java/com/pchengi/tasklistspro/ui/SwipeDeleteContainer.kt
- app/src/main/java/com/pchengi/tasklistspro/ui/TaskRow.kt
