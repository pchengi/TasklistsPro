# Drag handle manual test checklist

After applying this update and building the app:

1. Create three top-level tasks: A, B, C.
2. Drag B's handle upward. Expected: B moves above A.
3. Drag B's handle downward. Expected: B moves back below A.
4. Drag B's handle right while it has a visible task above it. Expected: B becomes a child of that previous visible task.
5. Drag B's handle left. Expected: B is promoted one level.
6. Create nested subtasks and confirm dragging right does not exceed three subtask levels.
7. Confirm tapping the title edits normally.
8. Confirm long-pressing the title toggles bold.
9. Confirm the checkbox completion rules still work.
