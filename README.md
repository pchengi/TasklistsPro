# Tasklists Pro

A phone-first Android task outliner built with Kotlin, Jetpack Compose, Room, and XML import/export.

## Current interaction model

- There is only one kind of item: a task.
- Tasks may contain subtasks up to three subtask levels deep.
- Tapping the floating `+` creates a new top-level task immediately and focuses it for typing.
- Tapping the small `+` under a task's delete icon creates a new child task immediately and focuses it for typing.
- There is no add-task dialog and no subtask menu.
- Delete removes the selected task and all of its subtasks.
- Marking a parent task done also marks all descendants done.
- Use the drag handle at the left of a task:
  - Drag up/down: reorder among sibling tasks.
  - Drag right: make it a subtask of the visible item above it, where allowed.
  - Drag left: promote it one level up.
- Long-press the task title to toggle bold formatting. Bold status is saved in the database and XML backups.
- XML import/export is available from the top app bar.

## Build

Open this folder in Android Studio and build the debug APK:

```bash
./gradlew assembleDebug
```

The APK will be created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The project pins Java/Kotlin to JVM 17 to avoid KSP JVM-target mismatches.

## Backup and restore

Use the top-right download icon to create an XML backup file. Use the top-right upload icon to restore from XML. The restore picker accepts common XML MIME types and also `*/*`, because some Android file managers label `.xml` files inconsistently.

If **Replace existing tasks when restoring** is checked, the restore replaces the current task tree. Otherwise, imported tasks are added to the existing tasks. The importer supports both the current attribute-based XML format and the older simple `<task>Title</task>` body-text format.
