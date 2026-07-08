# Restore icon + delete-all overlay

Base commit:

`5682df52ef325a1dbe657c58fb4629a47bad1c32`

Files included:

- `app/src/main/java/com/pchengi/tasklistspro/ui/TaskListScreen.kt`
- `app/src/main/java/com/pchengi/tasklistspro/viewmodel/TaskViewModel.kt`
- `app/src/main/java/com/pchengi/tasklistspro/repository/TaskRepository.kt`

Changes:

1. Replaces the XML restore/import toolbar icon with `Icons.Rounded.Restore`.
2. Adds a delete-all floating action button next to the main add button.
3. Delete-all shows a confirmation dialog before clearing the full task list.
4. Adds `deleteAll()` to `TaskViewModel` and `TaskRepository`.

Manual test:

1. Build with `./gradlew assembleDebug`.
2. Confirm toolbar restore icon is a looping restore arrow.
3. Add a few tasks and subtasks.
4. Tap trash FAB.
5. Confirm cancel leaves the list unchanged.
6. Tap trash FAB again and confirm "Delete everything" clears the list.
7. Confirm XML export/restore still works.
