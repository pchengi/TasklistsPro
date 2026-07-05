# Tasklists Pro

Tasklists Pro is a phone-first Android task outliner built with Kotlin, Jetpack Compose, Material 3, Room, and a single task hierarchy model.

## Current implemented behavior in this bulk update

- One object type: `TaskEntity`.
- Tasks can contain subtasks up to three subtask levels deep.
- Parent completion cascades down to descendants.
- Child completion synchronizes upward: if all children are checked, the parent is checked; if one child is unchecked, ancestors become unchecked while siblings stay unchanged.
- Inline, borderless task-title editing using `BasicTextField`.
- Tapping `+` creates a top-level task and requests focus on the new row.
- Tapping a row-level `+` creates a child task and requests focus on the new row.
- Long-pressing the task title toggles the task's bold flag.
- The task row is split into smaller composables: `TaskListScreen`, `TaskRow`, `InlineTaskTitle`, `TaskDragHandle`, and `EmptyState`.
- The drag handle is now an interaction target. Current behavior is deliberately simple:
  - drag handle upward: move task up among siblings;
  - drag handle downward: move task down among siblings;
  - drag handle right: indent under the previous visible task, when valid;
  - drag handle left: outdent one level.

This bulk update is intended to be pushed as a larger baseline so smaller GitHub issue-driven fixes can follow.

## Build

Open in Android Studio or run:

```bash
gradle assembleDebug
```

The repository currently uses the hosted Gradle installation in CI rather than a checked-in Gradle wrapper. Adding the wrapper is a good next maintenance step.

## Known follow-up work

- Confirm the drag-handle gesture feels good on a physical device.
- Add undo for delete.
- Add XML import/export.
- Add a Gradle wrapper.
- Add tests for completion propagation and movement rules.
