# XML import/export baseline testing

Base commit:

`75c20ea34523a4533da8920bc415dd6baa0ff7`

Apply this overlay, then run:

```bash
./gradlew assembleDebug
```

Manual test:

1. Add a few top-level tasks.
2. Add nested subtasks.
3. Mark some items done.
4. Double-tap one item to make it bold.
5. Collapse one parent.
6. Tap export in the app bar and save `tasklistspro-backup.xml`.
7. Delete or change some tasks.
8. Tap import in the app bar and select the exported XML file.
9. Confirm the task tree, checked states, bold states, collapsed states, and ordering are restored.
