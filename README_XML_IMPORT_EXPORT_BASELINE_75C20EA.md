# XML import/export restore overlay

Base commit:

`75c20ea34523a4533da8920bc415dd6baa0ff7`

Files included:

- `app/src/main/java/com/pchengi/tasklistspro/ui/TaskListScreen.kt`
- `app/src/main/java/com/pchengi/tasklistspro/viewmodel/TaskViewModel.kt`
- `app/src/main/java/com/pchengi/tasklistspro/repository/TaskRepository.kt`
- `docs/xml-import-export-testing.md`

Behavior:

- App bar export button saves a UTF-8 XML backup.
- App bar import button restores from a selected XML file.
- Import replaces all current tasks.
- XML preserves:
  - id
  - parentId
  - title
  - completed
  - bold
  - expanded
  - sortOrder
  - createdAt
  - updatedAt
